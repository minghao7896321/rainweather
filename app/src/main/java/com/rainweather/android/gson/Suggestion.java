package com.rainweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lenovo on 2017/4/5.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    public class Comfort {

        @SerializedName("brf")
        public String comfortindex;

        @SerializedName("txt")
        public String comfortinfo;

    }

    @SerializedName("cw")
    public CarWash carWash;

    public class CarWash {

        @SerializedName("brf")
        public String carWashindex;

        @SerializedName("txt")
        public String carWashinfo;

    }

    @SerializedName("drsg")
    public Dress dress;

    public class Dress {

        @SerializedName("brf")
        public String drsgindex;

        @SerializedName("txt")
        public String drsginfo;

    }

    public Flu flu ;

    public class Flu {

        @SerializedName("brf")
        public String fluindex;

        @SerializedName("txt")
        public String fluinfo;

    }

    public Sport sport ;

    public class Sport {

        @SerializedName("brf")
        public String sportindex;

        @SerializedName("txt")
        public String sportinfo;

    }

    @SerializedName("trav")
    public Travel travel ;

    public class Travel{

        @SerializedName("brf")
        public String travindex;

        @SerializedName("txt")
        public String travinfo;

    }

}
