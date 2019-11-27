def dropIndex(graph, indexName) {
    mgmt = graph.openManagement()
    idx = mgmt.getGraphIndex(indexName)
    mgmt.updateIndex(idx), SchemaAction.DISABLE_INDEX).get()
    mgmt.commit()
    graph.tx().commit()
    ManagementSystem.awaitGraphIndexStatus(graph, indexName).statis(SchemaStatus.DISABLED).call()

    mgmt = graph.openManagement()
    delIndex = mgmt.getGraphIndex(indexName)
    mgmt.updateIndex(delIndex, SchemaAction.REMOVE_INDEX)
    mgmt.commit()
    graph.tx().commit()
}
