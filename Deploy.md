# Deploy

### snapshot deploy

mvn -DaltDeploymentRepository=snapshot-repo::default::file:../maven-repo/snapshots clean deploy


### release deploy

mvn -DaltDeploymentRepository=release-repo::default::file:../maven-repo/releases clean deploy