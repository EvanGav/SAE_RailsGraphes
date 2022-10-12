package fr.umontpellier.iut.graphes;


import java.util.ArrayList;

public class Graphe {
    /**
     * matrice d'adjacence du graphe, un entier supérieur à 0 représentant la distance entre deux sommets
     * mat[i][i] = 0 pour tout i parce que le graphe n'a pas de boucle
     */
    private final int[][] mat;

    /**
     * Construit un graphe à n sommets
     *
     * @param n le nombre de sommets du graphe
     */
    public Graphe(int n) {
        mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = 0;
            }
        }
    }

    /**
     * @return le nombre de sommets
     */
    public int nbSommets() {
        return mat.length;
    }

    /**
     * Supprime l'arête entre les sommets i et j
     *
     * @param i un entier représentant un sommet
     * @param j un autre entier représentant un sommet
     */
    public void supprimerArete(int i, int j) {
        mat[i][j] = 0;
        mat[j][i] = 0;
    }

    /**
     * @param i un entier représentant un sommet
     * @param j un autre entier représentant un sommet
     * @param k la distance entre i et j (k>0)
     */
    public void ajouterArete(int i, int j, int k) {
        mat[i][j] = k;
        mat[j][i] = k;
    }

    /***
     * @return le nombre d'arête du graphe
     */
    public int nbAretes() {
        int nbAretes = 0;
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                if (mat[i][j] > 0) {
                    nbAretes++;
                }
            }
        }
        return nbAretes;
    }

    /**
     * @param i un entier représentant un sommet
     * @param j un autre entier représentant un sommet
     * @return vrai s'il existe une arête entre i et j, faux sinon
     */
    public boolean existeArete(int i, int j) {
        if(mat[i][j]>0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * @param v un entier représentant un sommet du graphe
     * @return la liste des sommets voisins de v
     */
    public ArrayList<Integer> voisins(int v) {
        ArrayList<Integer> voisin=new ArrayList<>();
        for(int j=0;j<mat.length;j++){
            if(mat[v][j]>0){
                voisin.add(j);
            }
        }
        return voisin;
    }

    /**
     * @return une chaîne de caractères permettant d'afficher la matrice mat
     */
    public String toString() {
        StringBuilder res = new StringBuilder("\n");
        for (int[] ligne : mat) {
            for (int j = 0; j < mat.length; j++) {
                String x = String.valueOf(ligne[j]);
                res.append(x);
            }
            res.append("\n");
        }
        return res.toString();
    }

    /**
     * Calcule la classe de connexité du sommet v
     *
     * @param v un entier représentant un sommet
     * @return une liste d'entiers représentant les sommets de la classe de connexité de v
     */
    public ArrayList<Integer> calculerClasseDeConnexite(int v) {
        ArrayList<Integer> classeCo=new ArrayList<>();
        classeCo.add(v);
        ArrayList<Integer> voisin=voisins(v);
        if(!voisin.isEmpty()){
            for(int i=0;i<voisin.size();i++){
                if(!classeCo.contains(voisin.get(i))){
                    classeCo.add(voisin.get(i));
                    voisin.addAll(voisins(voisin.get(i)));
                }

            }
            voisin.remove(0);
        }
        return classeCo;
    }

    /**
     * @return la liste des classes de connexité du graphe
     */
    public ArrayList<ArrayList<Integer>> calculerClassesDeConnexite() {
        ArrayList<ArrayList<Integer>> classes=new ArrayList<>();
        ArrayList<Integer> sommets=new ArrayList<>();
        for(int i=0;i<nbSommets();i++){
            sommets.add(i);
        }
        while(!sommets.isEmpty()){
            classes.add(calculerClasseDeConnexite(sommets.get(0)));
            sommets.removeAll(calculerClasseDeConnexite(sommets.get(0)));

        }
        return classes;
    }

    /**
     * @return le nombre de classes de connexité
     */
    public int nbCC() {
        return calculerClassesDeConnexite().size();
    }


    /**
     * @param u un entier représentant un sommet
     * @param v un entie représentant un sommet
     * @return vrai si (u,v) est un isthme, faux sinon
     */
    public boolean estUnIsthme(int u, int v) {
        int nAvant=nbCC();
        int distance=mat[u][v];
        supprimerArete(u,v);
        int n=nbCC();
        ajouterArete(u,v,distance);
        return n!=nAvant;
    }

    private Graphe(ArrayList<Integer> classe, int n, Graphe g){
        mat=new int[n][n];
        for(int i : classe){
            for(int j=0;j<n;j++){
                mat[i][j]=g.mat[i][j];
            }
        }
    }

    public int poidPlusLongCheminClasse(ArrayList<Integer> classe){
        Graphe g =new Graphe(classe, nbSommets(), this);
        int poid=0;
        int poidM=0;
        if(classe.size()>1) {
            int i = classe.get(0);
            ArrayList<Integer> copieClasse=classe;
            int k = 0;
            while (copieClasse.size()!=1) {
                ArrayList<Integer> voisin = g.voisins(i);
                for (int j : voisin) {
                    if (g.mat[i][j] > poidM) {
                        poidM = g.mat[i][j];
                        k = j;
                    }
                }
                g.supprimerArete(i, k);
                poid += poidM;
                i = k;
                poidM = 0;
                copieClasse=g.calculerClasseDeConnexite(k);
            }
            return poid;
        }
        return 0;
    }

    public ArrayList<Integer> classePlusLongue(){
        ArrayList<ArrayList<Integer>> classes=calculerClassesDeConnexite();
        ArrayList<Integer> classe=new ArrayList<>();
        if(classes.size()-nbSommets()>-nbSommets()+1){
            int m=0;
            for(ArrayList<Integer> a : classes){
                if(m< poidPlusLongCheminClasse(a)){
                    m= poidPlusLongCheminClasse(a);
                    classe=a;
                }
            }
        }
        else{
            classe=classes.get(0);
        }
        return classe;
    }

    /**
     * Calcule le plus long chemin présent dans le graphe
     *
     * @return une liste de sommets formant le plus long chemin dans le graphe sans repasser
     */
    public ArrayList<Integer> plusLongChemin() {
        ArrayList<Integer> plusLong=new ArrayList<>();
        ArrayList<Integer> classe=classePlusLongue();
        Graphe g=new Graphe(classe,nbSommets(),this);
        ArrayList<Integer> copieClasse=classePlusLongue();
        int poidM=0;
        int i=classe.get(0);
        plusLong.add(i);
        int k=0;
        while(copieClasse.size()!=1) {
            ArrayList<Integer> voisin = g.voisins(i);
            for (int j : voisin) {
                if (g.mat[i][j] > poidM) {
                    poidM = g.mat[i][j];
                    k = j;
                }
            }
            g.supprimerArete(i, k);
            plusLong.add(k);
            poidM = 0;
            i = k;
            copieClasse=g.calculerClasseDeConnexite(k);
        }
        return plusLong;
    }

    public int poidCheminPlusLong(){
        ArrayList<Integer> chemin=plusLongChemin();
        int s=0;
        for(int i=0;i<chemin.size()-1;i++){
            s+= mat[chemin.get(i)][chemin.get(i+1)];
        }
        return s;
    }

    private ArrayList<Integer> degres(){
        ArrayList<Integer> d=new ArrayList<>();
        for(int i=0;i<nbSommets(); i++){
            d.add(voisins(i).size());
        }
        return d;
    }

    /**
     * @return vrai s'il existe un parcours eulérien dans le graphe, faux sinon
     */
    public boolean existeParcoursEulerien() {
        int impair=0;
        ArrayList<Integer> d=degres();
        for(int i : d){
            if(i%2==1){
                impair ++;
            }
        }
        return nbCC()==1 && impair<2;
    }

    /**
     * @return vrai si le graphe est un arbre, faux sinon
     */
    public boolean estUnArbre() {
        return nbCC()==1 && nbAretes()==nbSommets()-1;
    }

    public boolean estForet(){
        for(ArrayList<Integer> a: calculerClassesDeConnexite()){
            if(!new Graphe(a,nbSommets(),this).estUnArbre()){
                return false;
            }
        }
        return true;
    }
}

