mvn clean source:jar deploy -DuniqueVersion=false -Dmaven.source.useDefaultManifestFile=true -Dmaven.source.includePom=true -Dmaven.install.skip=true -DdeplyAtEnd=true -DaltDeploymentRepository='github::default::https://maven.pkg.github.com/altinntech/bunny'
