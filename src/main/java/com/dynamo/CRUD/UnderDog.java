package com.dynamo.CRUD;

public class UnderDog  {
   public static boolean f1(){

       int sum1 = 0;

       for (int i = 1; i <= 3; i++)
       {
           sum1 = sum1 + 2 * i;
       }

       int sum2 = 0;

       for (int i = 4; i <= 10; i += 2)
       {
           sum2 = sum2 + i / 2;
       }

       return sum2 > sum1;

   }

    public static boolean f2(){

        boolean previous = f1();

        int sum = 0;

        if (false || true || (sum / 0) == 0)
            previous = !previous;

        if (previous ^ !previous)
            previous = !previous;

        return previous;


    }



    public static void main(String args[]){
        System.out.println(f1());
        System.out.println(f2());
    }
}
