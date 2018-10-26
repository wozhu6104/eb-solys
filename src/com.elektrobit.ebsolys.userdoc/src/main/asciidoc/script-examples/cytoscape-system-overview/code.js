/////////////////////////

var cy = window.cy = cytoscape({
  container: document.getElementById('cy'),

  boxSelectionEnabled: true,
  autounselectify: false,

  style: [
    {
      selector: 'node',
      css: {
        'content': 'data(id)',
        'text-valign': 'top',
        'text-halign': 'center'
      }
    },
    {
      selector: '$node > node',
      css: {
        'padding-top': '10px',
        'padding-left': '10px',
        'padding-bottom': '10px',
        'padding-right': '10px',
        'text-valign': 'top',
        'text-halign': 'center',
        'background-color': '#ccc'
      }
    },
    {
      selector: '$node > node > node',
      css: {
        'padding-top': '10px',
        'padding-left': '10px',
        'padding-bottom': '10px',
        'padding-right': '10px',
        'text-valign': 'top',
        'text-halign': 'center',
        'background-color': '#eee'
      }
    },
    {
      selector: 'edge',
      css: {
        'content': 'data(label)',
        'target-arrow-shape': 'triangle',
        'source-arrow-shape': 'triangle'
      }
    },
    {
      selector: ':selected',
      css: {
        'background-color': 'turquoise',
        'line-color': 'turquoise',
        'target-arrow-color': 'turquoise',
        'source-arrow-color': 'turquoise'
      }
    }
  ],

  elements: blockElements,

  layout: {
    name: 'cose-bilkent',
    nodeDimensionsIncludeLabels: true,
    //number of ticks per frame; higher is faster but more jerky
    refresh: 0,
    //Whether to fit the network view after when done
    fit: true,
    //Padding on fit
    padding: 30,
    //Whether to enable incremental mode
    randomize: false,
    //Node repulsion (non overlapping) multiplier
    nodeRepulsion: 10,
    //Ideal (intra-graph) edge length
    idealEdgeLength: 1,
    //Divisor to compute edge forces
    edgeElasticity: 0.1,
    //Nesting factor (multiplier) to compute ideal edge length for inter-graph edges
    nestingFactor: 2.0,
    //Gravity force (constant)
    gravity: 1.3,
    //Maximum number of iterations to perform
    numIter: 250,
    //Whether to tile disconnected nodes
    tile: true,
    //Type of layout animation. The option set is {'during', 'end', false}
    animate: false,
    //Amount of vertical space to put between degree zero nodes during tiling (can also be a function)
    tilingPaddingVertical: 30,
    //Amount of horizontal space to put between degree zero nodes during tiling (can also be a function)
    tilingPaddingHorizontal: 30,
    //Gravity range (constant) for compounds
    gravityRangeCompound: 2.5,
    //Gravity force (constant) for compounds
    gravityCompound: 2.5,
    //Gravity range (constant)
    gravityRange: 2.5,
    //Initial cooling factor for incremental layout
    initialEnergyOnIncremental: 1.5
  }
});

var api = cy.expandCollapse({
					layoutBy: {
					name: 'cose-bilkent',
					nodeDimensionsIncludeLabels: true,
					//number of ticks per frame; higher is faster but more jerky
					refresh: 0,
					//Whether to fit the network view after when done
					fit: true,
					//Padding on fit
					padding: 30,
					//Whether to enable incremental mode
					randomize: false,
					//Node repulsion (non overlapping) multiplier
					nodeRepulsion: 10,
					//Ideal (intra-graph) edge length
					idealEdgeLength: 1,
					//Divisor to compute edge forces
					edgeElasticity: 0.1,
					//Nesting factor (multiplier) to compute ideal edge length for inter-graph edges
					nestingFactor: 2.0,
					//Gravity force (constant)
					gravity: 1.3,
					//Maximum number of iterations to perform
					numIter: 250,
					//Whether to tile disconnected nodes
					tile: true,
					//Type of layout animation. The option set is {'during', 'end', false}
					animate: false,
					//Amount of vertical space to put between degree zero nodes during tiling (can also be a function)
					tilingPaddingVertical: 30,
					//Amount of horizontal space to put between degree zero nodes during tiling (can also be a function)
					tilingPaddingHorizontal: 30,
					//Gravity range (constant) for compounds
					gravityRangeCompound: 2.5,
					//Gravity force (constant) for compounds
					gravityCompound: 2.5,
					//Gravity range (constant)
					gravityRange: 2.5,
					//Initial cooling factor for incremental layout
					initialEnergyOnIncremental: 1.5
					},
					fisheye: true,
					animate: true,
					undoable: false
				});

let node = cy.nodes().filter(':child :child').forEach(function(node){

  let contentFunc = function(data) {
    let div = document.createElement('div');
    div.style.zIndex = 1200;
    div.innerHTML = '<a onclick="handleClick(&#39;'+data.toLowerCase()+'&#39;);" href="#"><img src="componentgraph/icons/chart_line.png" width="20px"/></a>';
    document.body.appendChild( div );
    return div;
  };

  let popper = node.popper({
    content: contentFunc(node.id()),
    popper:{
      placement: 'bottom-end'
    },
  });

  let update = function() {
  popper.scheduleUpdate();
};

  node.on('position', update);

  cy.on('pan zoom resize', update);
});


function handleClick(channelName){
  callSolysScript("DemoScript", "execute", channelName);
}
