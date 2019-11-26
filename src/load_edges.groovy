graph = JanusGraphFactory.open("/code/janusgraph.properties");
fh = new File('/code/data/edgesParcial.csv');

graph.makeEdgeLabel("route").multiplicity(Multiplicity.SIMPLE).make();
graph.makeEdgeLabel("contains").multiplicity(Multiplicity.SIMPLE).make();

graph.makePropertyKey('dist').dataType(Long.class).make();

def createEdge(line, headers, graph, vertex) {
    not_included_params = ["label", "src", "dst"]
    map = [:];
    for (i = 0; i < line.size(); i++) {
        map[headers[i]] = line[i];
    }
    
    v1 = null
    v2 = null
    if (vertex.containsKey(map["src"])) {
        v1 = vertex[map["src"]]
    } else {
        v1 = graph.traversal().V().has("id", map["src"])[0];
        vertex[map["src"]] = v1
    }
    if (vertex.containsKey(map["dst"])) {
        v2 = vertex[map["dst"]]
    } else {
        v2 = graph.traversal().V().has("id", map["dst"])[0];
        vertex[map["dst"]] = v2
    }
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
vertex = [:]
i = 0;
n = 1
fh.eachLine({ l ->
    line = l.split(";")
    if (headers.size() == 0) {
        for (header in line) {
            headers.add(header);
        }
    } else {
        createEdge(line, headers, graph, vertex);
    }
    i += 1
    if (i == 100) {
        graph.tx().commit();
        i = 0;
    }
    println(n);
    n += 1;
});

graph.tx().commit();
