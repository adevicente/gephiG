/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.grafo;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.FilterController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterator;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterator;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractColorTransformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;

/**
 *
 * @author alex
 */
public class GephiG implements Serializable {
    
    private int id;
    private String nombre;
    private GraphModel graphModel;
    private Workspace workspace;
    ProjectController pc;
    AttributeModel attributeModel;
    PreviewModel model;
    RankingController rankingController;
    ExportController ec;
    private ArrayList<String> nodos;
    private ArrayList<String> aristas;

    public GephiG() {
        pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
//Get models and controllers for this new workspace - will be useful later
        attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
        rankingController = Lookup.getDefault().lookup(RankingController.class);
        ec = Lookup.getDefault().lookup(ExportController.class);
        //Aplicamos propiedades
        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        model.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        //model.getProperties().putValue(PreviewProperty.NODE_LABEL_SHOW_BOX, true);
        //model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GREEN));
        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.2f));
        model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(6));

        NodeIterator todosNodos = this.getGraphmodel().getGraph().getNodes().iterator();
        nodos = new ArrayList<>();

        while (todosNodos.hasNext()) {
            Node nodo = todosNodos.next();
            nodos.add(nodo.toString());
        }
        
        nombre= "noFich";
    }

    public GephiG(File fichero) {

        pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
        //Get models and controllers for this new workspace - will be useful later
        attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
        rankingController = Lookup.getDefault().lookup(RankingController.class);
        ec = Lookup.getDefault().lookup(ExportController.class);
        //Aplicamos propiedades
        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        model.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        //model.getProperties().putValue(PreviewProperty.NODE_LABEL_SHOW_BOX, true);
        //model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GREEN));
        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.2f));
        model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(6));

        if (this.importar(fichero, graphModel)) {
        } else {

        };
        
        nombre = fichero.getName();

        /*
         //Filter              
         DegreeRangeFilter degreeFilter = new DegreeRangeFilter();
         degreeFilter.init(graphModel.getGraph());
         degreeFilter.setRange(new Range(20, Integer.MAX_VALUE));     //Remove nodes with degree < 30
         Query query = filterController.createQuery(degreeFilter);
         GraphView view = filterController.filter(query);
         graphModel.setVisibleView(view);    //Set the filter result as the visible view 
         //
         */
//Get Centrality
        GraphDistance distance = new GraphDistance();
        distance.setDirected(true);
        distance.execute(graphModel, attributeModel);

        NodeIterator todosNodos = this.getGraphmodel().getGraph().getNodes().iterator();
        nodos = new ArrayList<>();

        while (todosNodos.hasNext()) {
            Node nodo = todosNodos.next();
            nodos.add(nodo.toString());
        }
        
         EdgeIterator todasAristas = this.getGraphmodel().getGraph().getEdges().iterator();
        aristas = new ArrayList<>();

        while (todasAristas.hasNext()) {
            Edge edge = todasAristas.next();
            aristas.add(formatoArista(edge));
        }

        /*
         //Rank size by centrality
         AttributeColumn centralityColumn = attributeModel.getNodeTable().getColumn(GraphDistance.CLOSENESS);
         Ranking centralityRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, centralityColumn.getId());
         AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
         sizeTransformer.setMinSize(3);
         sizeTransformer.setMaxSize(10);
         rankingController.transform(centralityRanking, sizeTransformer);

         */
//Preview
    }

    public void aplicarYifanHu() {
        //Run YifanHuLayout for 100 passes - The layout always takes the current visible view      
        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(200f));
        layout.setGraphModel(graphModel);
        layout.resetPropertiesValues();
        layout.setOptimalDistance(75f);
        layout.initAlgo();
        for (int i = 0; i < 100 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }
        layout.endAlgo();
    }

    public void rankByDegree() {
        //Rank color by Degree
        Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
        AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_COLOR);
        colorTransformer.setColors(new Color[]{new Color(0xFEF000), new Color(0xB30000), new Color(0xB67890), new Color(0xFF7890)});
        rankingController.transform(degreeRanking, colorTransformer);
        AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
        sizeTransformer.setMinSize(3);
        sizeTransformer.setMaxSize(10);
        rankingController.transform(degreeRanking, sizeTransformer);
    }

    public boolean resaltar(String nombre, GraphModel modelo) {
        if (modelo.getGraph().getNode(nombre) != null) {
            modelo.getGraph().getNode(nombre).getNodeData().setColor(0f, 0f, 1f);
            NodeIterator vecinosDePhoP = modelo.getGraph().getNeighbors(modelo.getGraph().getNode(nombre)).iterator();
            while (vecinosDePhoP.hasNext()) {
                Node nodo = vecinosDePhoP.next();
                Edge edge;
                if (modelo.getGraph().getEdge(modelo.getGraph().getNode(nombre), nodo) != null) {
                    edge = modelo.getGraph().getEdge(modelo.getGraph().getNode(nombre), nodo);
                } else {
                    edge = modelo.getGraph().getEdge(nodo, modelo.getGraph().getNode(nombre));
                }
                nodo.getNodeData().setColor(0f, 0f, 1f);
                edge.getEdgeData().setColor(0f, 0f, 1f);
            }
            return true;
        } else {
            return false;
        }
    }

    public float compararAristas(GephiG grafo2) {

        ArrayList<String> nodos2 = grafo2.getNodos();

        /*Collections.sort(nodos, new Comparator<String>() {
         @Override
         public int compare(String nodo1, String nodo2) {
         return nodo1.compareTo(nodo2);
         }
         });*/

        ArrayList<String> aristas2 = grafo2.getAristas();

        ArrayList<String> sourceList = new ArrayList<>(this.getAristas());
        ArrayList<String> destinationList = new ArrayList<>(aristas2);

        sourceList.removeAll(aristas2);
        destinationList.removeAll(getAristas());

        sourceList.addAll(destinationList);

        float totalAristas = this.getGraphmodel().getGraph().getEdgeCount();
        float porcentaje = ((totalAristas - (float) sourceList.size()) * 100) / totalAristas;

        return porcentaje;
    }

    public boolean exportar(String nombreF) {
        //Export
        rankByDegree();
        aplicarYifanHu();
        System.out.println("Exportando...");
        try {
            if (nombreF == "pdf" || nombreF == "PDF") {
                ec.exportFile(new File("red.pdf"));
            } else if (nombreF == "png" || nombreF == "PNG") {
                ec.exportFile(new File("red.png"));
            } else if (nombreF == "pajek" || nombreF == "PAJEK") {
                ec.exportFile(new File("red.pajek"));
            } else if (nombreF == "csv" || nombreF == "CSV") {
                ec.exportFile(new File("red.csv"));
            } else if (nombreF == "gml" || nombreF == "GML") {
                ec.exportFile(new File("red.gml"));
            } else {
                JOptionPane.showMessageDialog(null, "Extensión no reconocida o no compatible");
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean importar(File file, GraphModel modelo) {
        //Import file       
        Container container;
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);

        try {
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), modelo.getWorkspace());
        return true;
    }

    /**
     * @return the graphmodel
     */
    public GraphModel getGraphmodel() {
        return graphModel;
    }

    /**
     * @param graphmodel the graphmodel to set
     */
    public void setGraphmodel(GraphModel graphmodel) {
        this.graphModel = graphmodel;
    }

    /**
     * @return the nodos
     */
    public ArrayList<String> getNodos() {

        return nodos;
    }

    private String formatoArista(Edge edge) {
        return (edge.getEdgeData().getSource().getLabel()
                + "-" + edge.getEdgeData().getTarget().getLabel());

    }

    /**
     * @return the aristas
     */
    public ArrayList<String> getAristas() {
       
        return aristas;
    }

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {

     //GephiG gephiG = new GephiG("RedTuberculosis.csv");
     //GephiG gephiG = new GephiG();
     //gephiG.importar("RedTuberculosis.csv", gephiG.getGraphmodel());
     //Pruebas
     //gephiG.resaltar("Rv0757", gephiG.getGraphmodel());
     //gephiG.exportar("pdf");
     //gephiG.exportar("png");
     //gephiG.exportar("error");
     JOptionPane.showMessageDialog(null, "Red exportada con éxito");
     }*/
}
