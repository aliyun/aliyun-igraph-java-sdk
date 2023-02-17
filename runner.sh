mvn compile
mvn exec:java -Dexec.mainClass="com.taobao.igraph.client.core.model.external.ClientCaseRunner" -Dexec.args="${1} localhost:22210 1.tmp"
