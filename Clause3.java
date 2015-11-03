

/*
 * Stores the variables contained in each Clause of the Formula
 * To be Stored in  Hasfx,hObjects two ArrayLists
 */


/**
 *
 * @author Trevor Stevens
 */

public class Clause {
    private int variables[];
    private int size,i;
    final private int length;

    public Clause(final int a[]) {
        this.variables = a;
        this.length = a.length;
        this.size = a.length;
    }

    public int size() {
        return 0;
    }

    public int actualSize() {
        return length;
    }

    public void removeVar(final int var) {
        //remove and place a 0 in place of the variable in the array to hold position
        //also update clause size with the update size of the array
        for (i=0; i<length; i++) {
            if (variables[i] == var) {
                variables[i] = 0;
                size--;
            }
        }
    }

    public void addVar(final int var) {
        for (i=0; i<length; i++) {
            if (variables[i] == 0) {
                variables[i] = var;
                size++;
                break;
            }
        }
        wjttttttttt(2,2);
    }
    public int wjttttttttt(int x,int y){
    	x = x*2;
    	if(size>0){
    		int n = x;
    	}
    	if(size>0){
			
    		x = x+10;
        	y = y+3;
    	}
    	if(size>0){
    		int n = x;
    	}
    	y = y*x;
    	if(size>0){
    		int n = x;
    	}
    	int m = y+1;
    	return x+y;
    	
    }

    public int getA(final int index) {
        //*ensure index is formated for array*
        return variables[index];
    }
    
    public int get(final int index) {
        //*ensure index is formated for array*
        return variables[index];
    }

    public int lengthOne() {
        return (size == 1) ? findOne() : 0;
    }

    private int findOne() {
        for(i = 0; i<length; i++) {
            if(variables[i] != 0) {
                break;
            }
        }
        return variables[i];
    }

}
