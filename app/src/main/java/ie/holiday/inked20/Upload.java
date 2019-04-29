package ie.holiday.inked20;

public class Upload {


    private String mName;
    private String mImageURL;


    public Upload() {
        //needed for firebase
    }


    public Upload(String name, String imageURL) {

        if (name.trim( ).equals("")) {

            name = "No Name";
        }

        mImageURL = imageURL;
        mName = name;


    }


    public String getName() {

        return mName;

    }

    public String getImageURL(){

        return  mImageURL;
    }


    public  void setName(String name){

        mName  = name;
    }

    public  void setImageURL(String imageURL){

        mImageURL = imageURL;
    }


}