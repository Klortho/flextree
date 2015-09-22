$(document).ready(function() {


  var svg = d3.select("#drawing").append("div").append('svg');
  var svg_g = svg.append("g");
  var last_id = 0;

  var nodes = [];
  function addNodes(kids) {
    if (typeof kids == "undefined" || kids == null || kids.length == 0) return;
    kids.forEach(function(k) {
      nodes.push(k);
      addNodes(k.children);
    })
  }

  d3.json("after.json", function(error, root) {
    if (error) throw error;

    nodes.push(root);
    addNodes(root.children);

    var node = svg_g.selectAll(".node")
        .data(nodes, function(d) { 
          return d.id || (d.id = ++last_id); 
        })
      .enter().append("g")
        .attr("class", "node")
    ;

    // Reposition everything according to the layout
    node.attr("transform", function(d) { 
        return "translate(" + d.x + "," + d.y + ")"; 
      })
      .append("rect")
        .attr("data-id", function(d) {
          return d.id;
        })
        .attr({
          x: function(d) { return -d.width/2; },
          y: 0,
          rx: 1,
          ry: 1,
          width: function(d) { return d.width; },
          height: function(d) { return d.height; },
        })
    ;

    // Set the svg drawing size and translation
    // Note that the x-y orientations between the svg and the tree drawing are reversed
    var min_x = null,
        max_x = null,
        min_y = null,
        max_y = null;
    var nodes_to_visit = [root],
        node;
    while ((node = nodes_to_visit.pop()) != null) {
      var nxmin = node.x - node.width / 2;
      min_x = min_x == null || nxmin < min_x ? nxmin : min_x;
      var nxmax = node.x + node.width / 2;
      max_x = max_x == null || nxmax > max_x ? nxmax : max_x;
      min_y = min_y == null || node.y < min_y ? node.y : min_y;
      var nymax = node.y + node.height;
      max_y = max_y == null || nymax > max_y ? nymax : max_y;
      var n, children;
      if ((children = node.children) && (n = children.length)) {
        while (--n >= 0) nodes_to_visit.push(children[n]);
      }
    }
    console.log("min_x = " + min_x + ", min_y = " + min_y + ", max_x = " + max_x + 
        ", max_y = " + max_y);
    svg.attr({
      width: max_x - min_x,
      height: max_y - min_y,
    });
    svg_g.attr("transform", "translate(" + (-min_x) + ", 0)");

  });

});