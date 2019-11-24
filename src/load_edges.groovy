graph = JanusGraphFactory.open("/code/janusgraph.properties");
fh = new File('/code/data/edgesParcial.csv');

graph.makeEdgeLabel("route").multiplicity(Multiplicity.SIMPLE).make();
graph.makeEdgeLabel("contains").multiplicity(Multiplicity.SIMPLE).make();

graph.makePropertyKey('dist').dataType(Long.class).make();

def createEdge(line, headers, graph) {
    not_included_params = ["label", "src", "dst"]
    map = [:];
    for (i = 0; i < line.size(); i++) {
        map[headers[i]] = line[i];
    }
    v1 = graph.traversal().V().has("id", map["src"])[0];
    v2 = graph.traversal().V().has("id", map["dst"])[0];
    e = v1.addEdge(map["label"], v2);
    for (header in headers) {
        if (not_included_params.contains(header)
            || !map.containsKey(header) || map[header] == "") {
            continue;
        }
        e.property(header, map[header]);
    }
};

headers = [];
fh.eachLine({ l ->
    line = l.split(";")
    if (headers.size() == 0) {
        for (header in line) {
            headers.add(header);
        }
    } else {
        createEdge(line, headers, graph);
    }
});

graph.tx().commit();