FROM ubuntu

ARG NODE_TYPE
ENV NODE_TYPE=$NODE_TYPE

RUN apt-get update && apt-get install -y --no-install-recommends \
      openjdk-8-jdk \
      net-tools \
      curl \
      netcat \
      gnupg \
      libsnappy-dev \
    && rm -rf /var/lib/apt/lists/*



RUN curl -O https://dist.apache.org/repos/dist/release/hadoop/common/KEYS

RUN gpg --import KEYS

ENV HADOOP_VERSION 3.2.1
ENV HADOOP_URL https://archive.apache.org/dist/hadoop/common/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
ENV HADOOP_HOME=/opt/hadoop-$HADOOP_VERSION
ENV HADOOP_CONF_DIR=/etc/hadoop
ENV MULTIHOMED_NETWORK=1
ENV USER=root
ENV PATH $HADOOP_HOME/bin/:$PATH


RUN set -x \
    && curl -fSL "$HADOOP_URL" -o /tmp/hadoop.tar.gz \
    && curl -fSL "$HADOOP_URL.asc" -o /tmp/hadoop.tar.gz.asc \
    && gpg --verify /tmp/hadoop.tar.gz.asc \
    && tar -xvf /tmp/hadoop.tar.gz -C /opt/ \
    && rm /tmp/hadoop.tar.gz*

RUN ln -s /opt/hadoop-$HADOOP_VERSION/etc/hadoop /etc/hadoop

RUN mkdir /opt/hadoop-$HADOOP_VERSION/logs

RUN mkdir /hadoop-data


ADD hadoop/config/entrypoint.sh /entrypoint.sh

RUN chmod a+x /entrypoint.sh



ENV HDFS_CONF_dfs_datanode_data_dir=file:///hadoop/dfs/data
RUN if [ "$NODE_TYPE" = "data" ]; then mkdir -p /hadoop/dfs/data; fi
VOLUME /hadoop/dfs/data

ENV HDFS_CONF_dfs_namenode_name_dir=file:///hadoop/dfs/name
RUN if [ "$NODE_TYPE" = "name" ]; then mkdir -p /hadoop/dfs/name; fi
VOLUME /hadoop/dfs/name

ENV YARN_CONF_yarn_timeline___service_leveldb___timeline___store_path=/hadoop/yarn/timeline
RUN if [ "$NODE_TYPE" = "historyserver" ]; then mkdir -p /hadoop/yarn/timeline; fi
VOLUME /hadoop/yarn/timeline


ADD hadoop/config/run.sh /run.sh
RUN chmod a+x /run.sh

EXPOSE 9864
EXPOSE 9870
#EXPOSE if [ "$NODE_TYPE" = "historyserver" ]; then 8188; fi
#EXPOSE if [ "$NODE_TYPE" = "nodemanager" ]; then 8042; fi
#EXPOSE if [ "$NODE_TYPE" = "resourcemanager" ]; then 8088; fi

CMD ["/run.sh"]

ENTRYPOINT ["/entrypoint.sh"]

#ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
#ENV HADOOP_HOME=/usr/local/hadoop
#ENV PATH=$PATH:/usr/local/hadoop/bin:/usr/local/hadoop/sbin
#
#RUN ssh-keygen -t rsa -f ~/.ssh/id_rsa -P '' && \
#    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
#
#RUN mkdir -p ~/hdfs/namenode && \
#    mkdir -p ~/hdfs/datanode && \
#    mkdir $HADOOP_HOME/logs
#
#COPY hadoop/config/* /temp/
#
#RUN mv /temp/ssh_config ~/.ssh/config && \
#    mv /temp/hadoop-env.sh /usr/local/hadoop/etc/hadoop/hadoop-env.sh && \
#    mv /temp/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml && \
#    mv /temp/core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml && \
#    mv /temp/mapred-site.xml $HADOOP_HOME/etc/hadoop/mapred-site.xml && \
#    mv /temp/yarn-site.xml $HADOOP_HOME/etc/hadoop/yarn-site.xml && \
#    mv /temp/slaves $HADOOP_HOME/etc/hadoop/slaves && \
#    mv /temp/start-hadoop.sh ~/start-hadoop.sh
#
#RUN chmod +x ~/start-hadoop.sh && \
#    chmod +x $HADOOP_HOME/sbin/start-dfs.sh && \
#    chmod +x $HADOOP_HOME/sbin/start-yarn.sh
#
## format namenode
#RUN /usr/local/hadoop/bin/hdfs namenode -format
#
#RUN ~/start-hadoop.sh
##CMD [ "sh", "-c", "service ssh start; bash"]
