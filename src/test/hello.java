package test;

/*
 * Stores the variables contained in each Clause of the Formula
 * To be Stored in  Hasfx,hObjects two ArrayLists
 */


/**
 *
 * @author Trevor Stevens
 */

public class hello {
    private int variables[];
    private int size,i;
    final private int length;

    public hello(final int a[]) {
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
 				size++;//***modified by new***
            }
        }
    }

    public void addVar(final int var) {
        for (i=0; i<length; i++) {
            if (variables[i] == 0) {
                variables[i] = var;
                size++;
                break;
 				z++;//***modified by new***
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
     		x = x+2;//***modified by new***
 			m-=1;//***modified by new***
        	y = y+10;
         	y = y+3;//***modified by patch***
    	}
    	if(size>0){
    		int n = x;
     		int n = y;//***modified by new***
    	}
    	y = y*x;
    	if(size>0){
    		int n = x;
    	}
    	int m = y+1;
    	return x+y;
    	
    }

     public int getA(final int index) {//***modified by patch***
         return variables[index];//***modified by patch***
     }//***modified by patch***
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

    @Override
    public String toString() {
        final String delimiter = " ";
        final int varLen = variables.length;
        final StringBuilder buf = new StringBuilder();
        for (int curVar = 0; curVar < varLen; ++curVar) {
            buf.append(variables[curVar]);
            buf.append(delimiter);
        }
        return buf.toString();
    }
}
