#!/bin/sh

echo "RemoteCmd: Start"

cfg_file="./script/RemoteCmd.cfg"

if [ ! -f $cfg_file ]; then
    echo "RemoteCmd: Failed, because $cfg_file does not exists" 1>&2
    exit 1
else
    source $cfg_file
    echo "RemoteCmd: rsync data to remote..."
    rsync -avzq --delete $rsync_local2remote_params . $remote_machine:$remote_dir &&	\
    # echo "RemoteCmd: ssh to remote and run cmd..." &&	\
	# ssh $remote_machine				\
	# 	"							\
	#     	cd $remote_dir &&		\
	#     	mvn compile             \
    # 	" && 						\
    # echo "RemoteCmd: rsync data back to local..." &&	         \
	# rsync -avzq --delete $rsync_remote2local_params              \
	# 	$remote_machine:$remote_dir/$rsync_remote2local_dir . && \
	# echo "RemoteCmd: Succeed" && 						\
	# exit 0 												\
	# || \
	# echo "RemoteCmd: rsync data back to local..." &&	         \
	# rsync -avzq --delete  $rsync_remote2local_params             \
	# 	$remote_machine:$remote_dir/$rsync_remote2local_dir . && \
	# echo "RemoteCmd: Failed"  1>&2 && \
	exit 1
fi
