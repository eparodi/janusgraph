def deleteIndex(indexName, graph){
	// Disable the "name" composite index
	management = graph.openManagement()
	nameIndex = management.getGraphIndex(indexName)
	management.updateIndex(nameIndex, SchemaAction.DISABLE_INDEX)
	management.commit()
	graph.tx().commit()

	// Block until the SchemaStatus transitions from INSTALLED to REGISTERED
	ManagementSystem.awaitGraphIndexStatus(graph, indexName).status(SchemaStatus.DISABLED).call()

	// Delete the index using JanusGraphManagement
	management = graph.openManagement()
	delIndex = management.getGraphIndex(indexName)
	future = management.updateIndex(delIndex, SchemaAction.REMOVE_INDEX)
	management.commit()
	graph.tx().commit()
}

