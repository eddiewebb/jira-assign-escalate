# Using Bitbucket Pipelines to Publish Atlassian Plugins

## Overview
This project uses pipelines to run CI builds and automatically pubish to the atlassian marketplace.

#### Build passes on `shipit`
![Passing Build in Bitbucket](/pipelines/assets/bitbucketpipeline.png "Passing Build in Bitbucket")

#### Version appears in marketplace!
![Published to Marketplace](/pipelines/assets/marketplaceversion.png "Published to Marketplace")



### Process:

1. Test every commit
2. Deploy snapshots of any version merged into `master`
3. Publish release to Atlassian marketplace anything merged into `shipit`


## Pipelines Manifest
```yaml
image: maven:3.3.3-jdk-7

pipelines:
  default:
    - step:
        script: # this Doesn't run on any branches matched below.
          - mvn --batch-mode clean install
  branches:
    master:
      - step:
          script: 
            - bash pipelines/setupSsh.sh  #validate SSH works before wasting time downloading internet
            - mvn --batch-mode deploy # this deploys a snapshot to the repo
    shipit:
      - step:
          script: # updates version as release
            - bash pipelines/setupSsh.sh  #validate SSH works before wasting time downloading internet
            - bash pipelines/shipit.sh #this script performs a release and publishes to marketplace
```

## Build
Just use the maven image, and ensure your pom points to maven.atlassian.com for repositories.  
```xml
<project ...>
   ...
    <repositories>
        <repository>
            <id>atlassian-public</id>
            <name>Atlassian Repository</name>
            <url>https://maven.atlassian.com/content/groups/public/</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>atlassian-public</id>
            <name>Atlassian Repository</name>
            <url>https://maven.atlassian.com/content/groups/public/</url>
        </pluginRepository>
    </pluginRepositories>
</project>
```

## Enable Maven Repository (snapshots and releases)

### decide repo strategy

#### No Repo
! If not using any repo, that's cool, just see [Marketplace API Docs](https://developer.atlassian.com/market/developing-for-the-marketplace/marketplace-api/examples-of-api-usage-through-json-requests/creating-an-add-on-version-using-json#Creatinganadd-onversionusingJSON-Step1:Uploadtheinstallableartifact,ifany) for instructions on uploading the local jar to atlassian first. 

You will need to:

1. remove "deploy" from [shipit.sh](/pipelines/shipit.sh) `mvn --batch-mode tag:scm`
2. modify the [shipit.sh](/pipelines/shipit.sh) URL pattern to point to atlassian


#### Person maven repo (SCP/SSH)
THis is the approach I use, 

1. set the PRIVATEKEY environment variable with a key that can scp to your server.
2. add the proper details in <distributionManagement> (add add wagon if using ssh)

```xml
<project ...>
   ...
    <distributionManagement>
        <repository>
            <uniqueVersion>false</uniqueVersion>
            <id>releases</id>
            <name>releases</name>
            <url>scp://user@domain.com/home/user/repository/releases</url>
            <layout>default</layout>
        </repository>
        <snapshotRepository>
            <uniqueVersion>true</uniqueVersion>
            <id>snapshots</id>
            <url>scp://user@domain.com/home/user/repository/snapshots</url>
            <name>snapshots</name>
        </snapshotRepository>
    </distributionManagement>
</project>
```

## Enable tagging
Specify tag name format

```xml
<project ...>
   ...
   <build>
        <plugins>
            <plugin>
                <artifactId>maven-scm-plugin</artifactId>
                <version>1.9.4</version>
                <configuration>
                    <tag>${project.artifactId}-${project.version}</tag>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Configuring publish
If you want to run shipit.sh you need to configure setting for the marketplace



## Set environment variables

1. PRIVATEKEY - a key with bitbucket (for tagging) access and optionally your maven repo (see below)
2. MKTUSER - user email for vendor admin
3. MKTPASSWD - password for user above (** use the secure feature)
4. MKTADDON - the full key of plugin found in marketplace URL
5. USERATHOST - if using a remote repo that needs ssh


##ship it!  ( to atlassian marketplace )
You can use any pipeline you want, these scripts assume that you checked in a pom with final versions.  I use the mprocess of merging master --> shipit branch as the time to change that value. If you ship right from master you can use the alternate approach to remove -SNAPSHOT and update the pom. (See shipit.sh)

1. Set environment variables in bamboo
1. Edit [marketplacePost.json](pipelines/marketplacePost.json) to change license, support level, etc -- leaving variables in place.
2. Confirm versioning scheme and URL used in [shipit.sh](/pipelines/shipit.sh)
3. merge latest code from `master` into `shipit` with the commit comment you want as release summary notes.
