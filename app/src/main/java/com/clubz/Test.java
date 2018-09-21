package com.clubz;

import java.util.List;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {
public static void main(String args[]){
    int n=6;


/*  12345
    1234
    123
    12
    1*/
    /*for (int i=n;i>0;i--){
    for (int j=1;j<i;j++){
        System.out.printf(""+j);
    }
    System.out.printf("\n");
}*/

   /*
    1
    12
    123
    1234
    12345
    123456*/
   /* for (int i=1;i<=n;i++){
        for (int j=1;j<=i;j++){
            System.out.printf(""+j);
        }
        System.out.printf("\n");
    }*/

   /*
     1
    12
   123
  1234
 12345
123456

    for (int i=1;i<=n;i++){
        for(int j=1;j<=n-i;j++){
            System.out.printf(" ");
        }
        for (int k=1;k<=i;k++){
            System.out.printf(""+k);
        }
        System.out.printf("\n");
    }*/
}


}
