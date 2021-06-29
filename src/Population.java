import org.omg.PortableInterceptor.INACTIVE;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//Author: Zachary Himed

/*-----------------------------------------------------------------
//The famous traveling salesman problem is solved here using a
// genetic algorithm. The program can simply be run by using the run
//button and there is no need to enter any input

//ENCODING SCHEME
Initial pop. is created and assigned fitness values using the fitness function
The Genetic algorithm takes in the initial pop. and creates children from this
pop. using the crossover function. Right after that, if the child is "lucky"
(2% chance), they will be mutated by the mutation function. Then, the children's
fitness will be evaluated and if one of them is the fittest, we will update them
to our most fit individual. New generations will be created until it has met the
pre-specified amount of generations
------------------------------------------------------------------*/
//This program is run with a predefined initial population of 100
// it also has a predefined generation amount of 100
//After testing and messing with different "knobs", I felt this is the
// most stable and optimal version of this genetic algorithm
//I found that when I raised the population amount and the generations
//I was able to get the better results and that's because with a greater
// initial population we have a more varied population and a higher chance
// of finding the optimal solution

//RESULTS
//The best result I found was a total fitness of 35
//The trip went like:  2 --> 1 --> 4 --> 9 --> 6 --> 8 --> 3 --> 7 --> 5 --> 2

class Individual{
    private ArrayList<Integer> genes=new ArrayList<>();
    private int fitness=0;

    Individual(){

    }

    Individual(ArrayList<Integer> g, int f){
        this.genes.addAll(g);
        this.fitness=f;
    }
    Individual(ArrayList<Integer> g){
        this.genes.addAll(g);
        this.fitness=0;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public ArrayList<Integer> getGenes() {
        return genes;
    }

    public void setGenes(ArrayList<Integer> genes) {
        this.genes.addAll(genes);
    }
    public void printGenes(){
        for (int i=0;i<9;i++){
            System.out.printf("%d",genes.get(i));
        }
        System.out.println();
    }
    public void printFitness(){
        System.out.println(fitness);
    }

}

public class Population {
    //the initial population
    ArrayList<Individual> initial = new ArrayList<>();
    //holds all the unique permutations
    HashSet<Integer> perm = new HashSet<>();
    //the initial population size
    int lInitial;
    //holds the shortest distance/most fit individual
    int mostFit = Integer.MAX_VALUE;
    //holds the cities paths
    ArrayList<Integer> bestPath =new ArrayList<>();
    //distance matrix
    int[][] matrix =new int[9][9];

    Population(int n){
        //initializes size of initial population
        this.lInitial=n;
        //calls createPop to create the initial population
        this.initial = createPop(lInitial);
                this.matrix = new int[][]{
                {0, 2, 11, 3, 18, 14, 20, 12, 5},
                {2, 0, 13, 10, 5, 3, 8, 20, 17},
                {11, 13, 0, 5, 19, 21, 2, 5, 8},
                {3, 10, 5, 0, 6, 4, 12, 15, 1},
                {18, 5, 19, 6, 0, 12, 6, 9, 7},
                {14, 3, 21, 4, 12, 0, 19, 7, 4},
                {20, 8, 2, 12, 6, 19, 0, 21, 13},
                {12, 20, 5, 15, 9, 7, 21, 0, 6},
                {5, 17, 8, 1, 7, 4, 13, 6, 0}
        };

        GeneticAlgorithm(initial);

    }

    private int createPerm(){
        boolean isThere=false;
        int num = 0;
        while (!isThere){
            HashSet<Integer> uniqueSet = new HashSet<>();
            int temp;
            temp = (int) (Math.random() * 10);
            if (temp == 0) {
                temp++;
            }
            uniqueSet.add(temp);
            num = num + temp;
            boolean flag;
            while (uniqueSet.size() < 9) {
                flag = false;
                while (!flag) {
                    temp = (int) (Math.random() * 10);
                    if (temp == 0) {
                        temp++;
                    }
                    if (uniqueSet.add(temp)) {
                        num = num * 10;
                        num = num + temp;
                        flag = true;
                    }
                }
            }
            if(perm.add(num)){
                isThere=true;
            }
        }

        return num;
    }

    //creates the initial population
    private ArrayList<Individual> createPop(int lInitial) {
        ArrayList<Individual> initials =new ArrayList<>();

        for(int i=0;i<lInitial;i++){
            int x=createPerm();
            //convert to arraylist of integers
            ArrayList<Integer> temp =new ArrayList<>();
            String a = String.valueOf(x);
            for(int j=0;j<9;j++){
                int n= Character.getNumericValue(a.charAt(j));
                temp.add(n);
            }

            initials.add(new Individual(temp));
            //clear temp for reuse
            temp.clear();
        }
        return initials;
    }

    public void printResult(){
        System.out.printf("%nThe most fit score is %d%n%n",mostFit);
        System.out.printf("Starting at city %d:%n",bestPath.get(0));
        for(int i=0;i<9;i++){
            System.out.printf("%d --> ",bestPath.get(i));
        }
        System.out.printf("%d%n",bestPath.get(0));

        for(int i =0;i<bestPath.size()-1;i++){
            System.out.printf("%d to %d is %d units%n",bestPath.get(i),bestPath.get(i+1),matrix[bestPath.get(i)-1][bestPath.get(i+1)-1]);
        }
        System.out.printf("%d to %d is %d units%n",bestPath.get(8),bestPath.get(0),matrix[bestPath.get(8)-1][bestPath.get(0)-1]);
        System.out.printf("For a total distance of %d units%n",mostFit);

    }


    public int Fitness(ArrayList<Integer> a){
        //map a fitness number to
        int first;
        int second;
        int distance=0;
        ArrayList<Integer> temp=new ArrayList<>();
        temp.addAll(a);
        for(int i=0;i<temp.size();i++){
            if(i!=8) {
                first = temp.get(i);
                second=temp.get(i+1);
                //calculate distance by referring to distance matrix
                distance+=matrix[second-1][first-1];

            }else{
                //we are on the last city so must return back to first
                first = temp.get(i);
                second=temp.get(0);
                //calculate distance by referring to distance matrix
                distance=distance+matrix[second-1][first-1];
            }
        }
        return distance;
    }

    public Individual Mutation(Individual child){
        // random number to decide the index to change the value
        int i= (int) (Math.random()*9);
        //random number to change that value to
        int j=(int) (Math.random()*9);

        //ArrayList<Integer> t = new ArrayList<>();
        int t= child.getGenes().get(i);
        child.getGenes().set(i,child.getGenes().get(j));
        child.getGenes().set(j,t);
        //System.out.printf("Child has mutation at %d to %d%n",i,j);

        return child;

    }

    public ArrayList<Individual> Crossover(Individual x, Individual y){
        HashSet<Integer> setX =new HashSet<>();
        HashSet<Integer> setY =new HashSet<>();

        ArrayList<Individual> a = new ArrayList<>();
        ArrayList<Integer> parX =x.getGenes();
        ArrayList<Integer> parY =y.getGenes();
        ArrayList<Integer> cX =new ArrayList<>();
        ArrayList<Integer> cY =new ArrayList<>();
        Individual childX = new Individual();
        Individual childY = new Individual();
        //crossover point is from index 2-6

        //creating first child
        cX.add(-1);
        cX.add(-1);
        cX.add(parY.get(2));
        cX.add(parY.get(3));
        cX.add(parY.get(4));
        cX.add(parY.get(5));
        cX.add(parY.get(6));
        cX.add(-1);
        cX.add(-1);
        setX.add(parY.get(2));
        setX.add(parY.get(3));
        setX.add(parY.get(4));
        setX.add(parY.get(5));
        setX.add(parY.get(6));

        //fill in the rest that aren't conflicting
        for(int i=0;i<2;i++){
            if(setX.add(parX.get(i))){
                cX.set(i,parX.get(i));
            }
        }
        for(int i=7;i<9;i++){
            if(setX.add(parX.get(i))){
                cX.set(i,parX.get(i));
            }
        }

        //go through arraylist and check for -1, meaning empty values
        boolean flag;
        for(int i=0;i<9;i++){
            flag=false;
            if(cX.get(i)==-1){
                while(!flag){
                    int num=(int) (Math.random()*10);
                    if(num==0){
                        num++;
                    }
                    if(setX.add(num)){
                        flag=true;
                        cX.set(i,num);
                    }

                }
            }
        }


        //creating second child
        cY.add(-1);
        cY.add(-1);
        cY.add(parX.get(2));
        cY.add(parX.get(3));
        cY.add(parX.get(4));
        cY.add(parX.get(5));
        cY.add(parX.get(6));
        cY.add(-1);
        cY.add(-1);
        setY.add(parX.get(2));
        setY.add(parX.get(3));
        setY.add(parX.get(4));
        setY.add(parX.get(5));
        setY.add(parX.get(6));


        //fill in the rest that aren't conflicting
        for(int i=0;i<2;i++){
            if(setY.add(parY.get(i))){
                cY.set(i,parY.get(i));
            }
        }
        for(int i=7;i<9;i++){
            if(setY.add(parY.get(i))){
                cY.set(i,parY.get(i));
            }
        }

        //go through arraylist and check for -1, meaning empty values
        for(int i=0;i<9;i++){
            flag=false;
            if(cY.get(i)==-1){
                while(!flag){
                    int num=(int) (Math.random()*10);
                    if(num==0){
                        num++;
                    }
                    if(setY.add(num)){
                        flag=true;
                        cY.set(i,num);
                    }

                }
            }
        }


        childX.setGenes(cX);
        childY.setGenes(cY);
        a.add(childX);
        a.add(childY);

        return a;
    }

    public void GeneticAlgorithm(ArrayList<Individual> initial){
        //holds the current population
        ArrayList<Individual> pop =new ArrayList<>();
        pop.addAll(initial);

        //calculating the fitness score and storing it with the individual
        for (int i=0;i<initial.size();i++){
            int fitScore =Fitness(initial.get(i).getGenes());
            initial.get(i).setFitness(fitScore);
            if(fitScore<mostFit){
                //update the mostFit var. because we found a shorter distance
                mostFit=fitScore;
                //update mostFitArray too
                bestPath.clear();
                bestPath.addAll(initial.get(i).getGenes());

            }
        }

        int numGenerations=100;
        System.out.printf("Genetic Algorithm with %d generations%n",numGenerations);
        System.out.printf("Initial Population of %d%n",initial.size());
        do{
            ArrayList<Individual> newPop = new ArrayList<>();
            for(int i=0;i<initial.size()/2;i++){
//                x &lt;- RANDOM-SELECTION(population, FITNESS-FN)
                int x = (int) (Math.random()*initial.size());
//      *       y &lt;- RANDOM-SELECTION(population, FITNESS-FN)
                int y = (int) (Math.random()*initial.size());
                if(x==y){
                    while(x==y){
                        y = (int) (Math.random()*initial.size());
                    }
                }

//      *       child &lt;- CROSSOVER(x, y)
                ArrayList<Individual> children = new ArrayList<>();
                children.addAll(Crossover(initial.get(x),initial.get(y)));
//      *       if (small random probability) then child &lt;- MUTATE(child)
                //There's a 2 percent chance of mutation on any individual
                for(int j=0;j<children.size();j++){
                    int rand = ((int) (Math.random()*100));
                    if(rand<=2){
                        //do some mutation
                        Mutation(children.get(j));
                    }

                }
                for (int j=0;j<2;j++){
                    int fitScore =Fitness(children.get(j).getGenes());
                    children.get(j).setFitness(fitScore);
                    if(fitScore<mostFit){
                        //update the mostFit var. because we found a shorter distance
                        mostFit=fitScore;
                        //update mostFitArray too
                        bestPath.clear();
                        bestPath.addAll(children.get(j).getGenes());
                    }
                }

//      *       add child to new_population
                newPop.addAll(children);
                children.clear();
            }

            numGenerations--;
            pop.clear();
            pop.addAll(newPop);

        }while (numGenerations!=0);

        printResult();

    }

    public static void main(String arg[]){
        Population test = new Population(100);
    }
}
