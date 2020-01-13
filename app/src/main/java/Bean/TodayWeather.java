package Bean;

import androidx.annotation.NonNull;

public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String fengli;
    private String fengxiang;
    private String date;
    private String high;
    private String low;
    private String type;


    public String getDate() {
        return date;
    }

    public String getCity() {
        return city;
    }

    public String getFengli() {
        return fengli;
    }

    public String getHigh() {
        return high;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }


    @NonNull
    @Override
    public String toString() {
        return "TodayWeather{"+
                "city='"+city+'\''+
                ",updatetime='"+updatetime+'\''+
                ",wendu='"+wendu+'\''+
                ",fengxiang='"+fengxiang+'\''+
                ",fengli='"+fengli+'\''+
                ",date='"+date+'\''+
                ",high='"+high+'\''+
                ",low='"+low+'\''+
                ",type='"+type+'\''+
                "}";
    }
}
