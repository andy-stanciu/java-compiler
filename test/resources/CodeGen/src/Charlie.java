class Main{
    public static void main(String[] args){
        new Start().pubs();
        System.out.println(new Start().pubs());

    }


}

class Start{
    int stem;
    public int pubs(){
        stem =13;
        stem *=6;
        return stem;
    }

}