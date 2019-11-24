graph = JanusGraphFactory.open("/code/janusgraph.properties");

mgmt = graph.openManagement()
mgmt.buildIndex("idIndex", Vertex.class).addKey(mgmt.getPropertyKey("id")).buildMixedIndex("search")
mgmt.commit()

ManagementSystem.awaitGraphIndexStatus(graph, "idIndex").call()
mgmt.commit()
