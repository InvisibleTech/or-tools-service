FROM centos:7

#############
##  SETUP  ##
#############
RUN yum -y update \
&& yum -y --enablerepo=extras install epel-release \
&& yum -y install yum-utils \
&& yum -y install python-pip \
&& pip install --upgrade pip \
&& yum -y install \
 wget git pkg-config make autoconf libtool zlib-devel gawk gcc-c++ curl subversion \
 redhat-lsb-core pcre-devel which \
 python-devel python-setuptools python-six python-wheel \
 java-1.8.0-openjdk  java-1.8.0-openjdk-devel \
&& yum clean all \
&& rm -rf /var/cache/yum \
&& pip install --upgrade ortools

RUN  wget https://github.com/google/or-tools/releases/download/v6.10/or-tools_centos-7_v6.10.6025.tar.gz \
&& tar -zxf or-tools_centos-7_v6.10.6025.tar.gz \
&& cd or-tools_CentOS-7.5.1804-64bit_v6.10.6025 \
&& make test_java

RUN yum install -y maven

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN ["mvn", "package"]

EXPOSE 4567
CMD ["/usr/bin/java", "-jar", "target/or-tools-server-jar-with-dependencies.jar"]
