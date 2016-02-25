package com.example.feeling.homework3.response;

/**
 * Created by feeling on 2/17/16.
 */
import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class MessageResponse {

    @SerializedName("result_list")
    @Expose
    private List<ResultList> resultList = new ArrayList<ResultList>();
    @SerializedName("result")
    @Expose
    private String result;

    /**
     *
     * @return
     * The resultList
     */
    public List<ResultList> getResultList() {
        return resultList;
    }

    /**
     *
     * @param resultList
     * The result_list
     */
    public void setResultList(List<ResultList> resultList) {
        this.resultList = resultList;
    }

    /**
     *
     * @return
     * The result
     */
    public String getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(String result) {
        this.result = result;
    }

}