#!/bin/bash

node_type=`echo $NODE_TYPE`

if [ "`echo $node_type`" == "data" ]; then
  datadir=`echo $HDFS_CONF_dfs_datanode_data_dir | perl -pe 's#file://##'`
  if [ ! -d $datadir ]; then
    echo "Datanode data directory not found: $datadir"
    exit 2
  fi

  $HADOOP_HOME/bin/hdfs --config $HADOOP_CONF_DIR datanode
fi

if [ "`echo $node_type`" == "name" ]; then
  namedir=`echo $HDFS_CONF_dfs_namenode_name_dir | perl -pe 's#file://##'`
  if [ ! -d $namedir ]; then
    echo "Namenode name directory not found: $namedir"
    exit 2
  fi

  if [ -z "$CLUSTER_NAME" ]; then
    echo "Cluster name not specified"
    exit 2
  fi

  echo "remove lost+found from $namedir"
  rm -r $namedir/lost+found

  if [ "`ls -A $namedir`" == "" ]; then
    echo "Formatting namenode name directory: $namedir"
    $HADOOP_HOME/bin/hdfs --config $HADOOP_CONF_DIR namenode -format $CLUSTER_NAME
  fi

  $HADOOP_HOME/bin/hdfs --config $HADOOP_CONF_DIR namenode
fi

if [ "`echo $node_type`" == "historyserver" ]; then
  $HADOOP_HOME/bin/yarn --config $HADOOP_CONF_DIR historyserver
fi

if [ "`echo $node_type`" == "nodemanager" ]; then
  $HADOOP_HOME/bin/yarn --config $HADOOP_CONF_DIR nodemanager
fi