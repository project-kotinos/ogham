<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>fr.aba</groupId>
	<artifactId>${project.artifactId}</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>${project.name}</name>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
	</properties>

	<dependencies>
		<dependency>
			<groupId>fr.sii.ogham</groupId>
			<artifactId>ogham-test-utils</artifactId>
			<version>${oghamVersion}</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>fr.sii.ogham</groupId>
			<artifactId>ogham-test-classpath-runtime</artifactId>
			<version>${oghamVersion}</version>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.22.2</version>
				<configuration>
					<systemPropertyVariables>
						<activeFacets>${activeFacets}</activeFacets>
					</systemPropertyVariables>
				</configuration>
			</plugin>
		</plugins>
	</build>
</project>
