def dropIndex(graph, indexName) {
    mgmt = graph.openManagement()
    idx = mgmt.getGraphIndex(indexName)
    mgmt.updateIndex(idx), SchemaAction.DISABLE_INDEX).get()
    mgmt.commit()
    ManagementSystem.awaitGraphIndexStatus(graph, indexName).statis(SchemaStatus.DISABLED).call()
}