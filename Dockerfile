FROM centos:7

# Install Python and JDK.
RUN yum -y update \
&& yum -y --enablerepo=extras install epel-release \
&& yum -y install yum-utils \
&& yum -y install java-11-openjdk.x86_64 java-11-openjdk-devel.x86_64 \
&& yum -y install \
 wget git pkg-config make autoconf libtool zlib-devel gawk gcc-c++ curl subversion \
  redhat-lsb-core pcre-devel which \
&& yum clean all \
&& rm -rf /var/cache/yum

# Get Maven but not using package manager which leads to Java 1.8 getting in the way.
RUN mkdir /Downloads \
&& cd /Downloads

RUN wget http://mirror.olnevhost.net/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz \
&& tar xvf apache-maven-3.3.9-bin.tar.gz \
&& rm apache-maven-3.3.9-bin.tar.gz \
&& mv apache-maven-3.3.9 /usr/local/apache-maven

ENV M2_HOME /usr/local/apache-maven
ENV M2 $M2_HOME/bin
ENV PATH $M2:$PATH
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-11.0.1.13-3.el7_6.x86_64

# Get OR Tools per the directions for installing on Linux.
RUN cd /Downloads \
&& wget https://github.com/google/or-tools/releases/download/v6.10/or-tools_centos-7_v6.10.6025.tar.gz \
&& tar -zxf or-tools_centos-7_v6.10.6025.tar.gz \
&& cd or-tools_CentOS-7.5.1804-64bit_v6.10.6025 \
&& make test_java

WORKDIR /code

# Prepare by downloading dependencies and install OR Tools jars to local repo.
ADD pom.xml /code/pom.xml
RUN mvn install:install-file -DgroupId=com.google -DartifactId=ortools -Dversion=6.10.6025 -Dfile=/Downloads/or-tools_CentOS-7.5.1804-64bit_v6.10.6025/lib/com.google.ortools.jar -Dpackaging=jar -DgeneratePom=true

# Adding source, compile and package into a fat jar.
ADD src /code/src
RUN mvn package

EXPOSE 8080

# Add OR Tools jars to Load Library path for JNI.
ENV LD_LIBRARY_PATH /Downloads/or-tools_CentOS-7.5.1804-64bit_v6.10.6025/lib
CMD /usr/bin/java -jar target/or-tools-server-jar-with-dependencies.jar
