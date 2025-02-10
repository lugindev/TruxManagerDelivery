package ch.parolini.truxmanager.delivery;

public class Context1 {
    private static MainActivity context = null;

    public Context1(MainActivity context){
        Context1.context = context;
    }

    public Context1(){

    }

    public static MainActivity getContext(){


        return context;
    }


}
