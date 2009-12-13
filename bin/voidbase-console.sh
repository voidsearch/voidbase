 #!/bin/bash

#
# Copyright 2009 VoidSearch.com
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#

# BUILD CLASSPATH
base_dir=$(dirname $0)/..
CLASSPATH=$(echo $base_dir/target/*.jar $base_dir/lib/*.jar | tr " " :)

# OTHER PARAMETERS
USER_ARGS="$@"
JVM_ARGS="-Xmx1024m -Xms256m"
EXTERNAL_LIBS="/usr/local/lib"
MAIN_CLASS="com.voidsearch.voidbase.console.VoidBaseConsole"

# RUN APPLICATION
echo "Starting: java -server -cp $CLASSPATH -Djava.library.path=${EXTERNAL_LIBS} ${JVM_ARGS} ${MAIN_CLASS} ${USER_ARGS}"
java -server -cp $CLASSPATH -Djava.library.path=${EXTERNAL_LIBS} ${JVM_ARGS} ${MAIN_CLASS} ${USER_ARGS}