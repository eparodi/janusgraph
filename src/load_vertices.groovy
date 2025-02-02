graph = JanusGraphFactory.open("/code/janusgraph.properties");
fh = new File('/code/data/cleanVertices.csv');

graph.makeVertexLabel("version").make();
graph.makeVertexLabel("airport").make();
graph.makeVertexLabel("country").make();
graph.makeVertexLabel("continent").make();

graph.makePropertyKey('id').dataType(Long.class).make();
graph.makePropertyKey('code').dataType(String.class).make();
graph.makePropertyKey('icao').dataType(String.class).make();
graph.makePropertyKey('city').dataType(String.class).make();
graph.makePropertyKey('desc').dataType(String.class).make();
graph.makePropertyKey('runways').dataType(Long.class).make();
graph.makePropertyKey('longest').dataType(Long.class).make();
graph.makePropertyKey('elev').dataType(Long.class).make();
graph.makePropertyKey('loc').dataType(Geoshape.class).make();

def createVertex(line, headers, graph) {
    not_included_params = ["label", "lat", "lon"]
    long_params = ["runways", "longest", "elev"]
    map = [:];
    for (i = 0; i < line.size(); i++) {
        map[headers[i]] = line[i];
    }

    v = graph.addVertex(map["label"]);
    if (map["label"] == "airport") {
	map["lat"] = map["lat"].toDouble()
        if (map["lat"] > 90 || map["lat"] < 90) {
            map["lat"] = map["lat"] / 10;
        }
        v.property('loc', Geoshape.point(map["lat"], map["lon"].toDouble()))
    }
    for (header in headers) {
        if (not_included_params.contains(header) || !map.containsKey(header) || map[header] == "") {
            continue;
        }
	if (long_params.contains(header)) {
            v.property(header, map[header].toDouble().longValue())
	    continue;
        }
        v.property(header, map[header]);
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
        createVertex(line, headers, graph);
    }
});

graph.tx().commit();
