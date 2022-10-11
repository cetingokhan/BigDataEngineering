FROM ubuntu

WORKDIR /root

RUN apt-get update && apt-get install -y openssh-server openjdk-8-jdk wget

RUN wget https://dlcdn.apache.org/hadoop/common/hadoop-3.2.3/hadoop-3.2.3.tar.gz && \
    tar -xzvf hadoop-3.2.3.tar.gz && \
    mv hadoop-3.2.3 /usr/local/hadoop && \
    rm hadoop-3.2.3.tar.gz

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV HADOOP_HOME=/usr/local/hadoop
ENV PATH=$PATH:/usr/local/hadoop/bin:/usr/local/hadoop/sbin

RUN ssh-keygen -t rsa -f ~/.ssh/id_rsa -P '' && \
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

RUN mkdir -p ~/hdfs/namenode && \
    mkdir -p ~/hdfs/datanode && \
    mkdir $HADOOP_HOME/logs

COPY hadoop/config/* /temp/

RUN mv /temp/ssh_config ~/.ssh/config && \
    mv /temp/hadoop-env.sh /usr/local/hadoop/etc/hadoop/hadoop-env.sh && \
    mv /temp/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml && \
    mv /temp/core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml && \
    mv /temp/mapred-site.xml $HADOOP_HOME/etc/hadoop/mapred-site.xml && \
    mv /temp/yarn-site.xml $HADOOP_HOME/etc/hadoop/yarn-site.xml && \
    mv /temp/slaves $HADOOP_HOME/etc/hadoop/slaves && \
    mv /temp/start-hadoop.sh ~/start-hadoop.sh

RUN chmod +x ~/start-hadoop.sh && \
    chmod +x $HADOOP_HOME/sbin/start-dfs.sh && \
    chmod +x $HADOOP_HOME/sbin/start-yarn.sh

# format namenode
RUN /usr/local/hadoop/bin/hdfs namenode -format
CMD [ "sh", "-c", "service ssh start; bash"]
