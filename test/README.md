# Maven Annotation plugin - test

## Steps for test 

Deploy project assets. (need only for the SNAPSHOT)
> let's go in root folder and run
>```dockerfile
>mvn deploy 
>```

Build docker image
> let's go in `test` folder and run
>```dockerfile
>mvn -Pdocker docker:build 
>```
 
Create docker volume
> let's go in `test` folder and run
>```dockerfile
>mvn -Pdocker docker:volume-create 
>```

Run test
> let's go in `test` folder and run
>```dockerfile
>mvn -Pdocker docker:rund 
>```
