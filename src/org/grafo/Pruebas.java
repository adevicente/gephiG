/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.grafo;

import java.awt.List;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterator;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.NodeIterator;
import static processing.core.PApplet.println;

/**
 *
 * @author alex
 */
public class Pruebas {

    GephiG grafo, grafo2;
    ArrayList<String> nodos, nodos2;
    ArrayList<String> aristas, aristas2;

    public Pruebas() {

        ArrayList<GephiG> repositorio = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            File red = new File("RedTuberculosis.csv");
            repositorio.add(new GephiG(red));
        }

        try {
            ObjectOutputStream ficheroSalida = new ObjectOutputStream(new FileOutputStream(new File("Repositorio.dat")));
            ficheroSalida.writeObject(repositorio);
            ficheroSalida.flush();
            ficheroSalida.close();

        } catch (FileNotFoundException fnfe) {

        } catch (IOException ioe) {

        }

        /*

         try {
         ObjectInputStream ficheroEntrada = new ObjectInputStream(new FileInputStream(new File("Repositorio.dat")));
         repositorio = (ArrayList) ficheroEntrada.readObject();
         ficheroEntrada.close();
         if (repositorio == null) {
         System.out.println("<Lista peliculas vacio>");
         } else {
         System.out.println("<Lista peliculas llena>");
         }

         } catch (ClassNotFoundException cnfe) {

         } catch (FileNotFoundException fnfe) {

         } catch (IOException ioe) {

         };

         /*File red = new File("RedTuberculosis.csv");
         File red2 = new File("RedTuberculosis2.csv");

         grafo = new GephiG(red);*/
        File red2 = new File("RedTuberculosis2.csv");
         grafo2 = new GephiG(red2);
        System.out.println(repositorio.size());

        float porcentaje = repositorio.get(0).compararAristas(grafo2);

        System.out.println("Porcentaje de similitud de regulación: " + porcentaje + "%");

    }

    public static void main(String args[]) {

        new Pruebas();

    }

}
