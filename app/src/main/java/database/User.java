package database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "inactive")
    private Integer inactive;

    @ColumnInfo(name = "fname")
    private String fname;

    @ColumnInfo(name = "lname")
    private String lname;

    @ColumnInfo(name = "dob")
    private String dob;

    @ColumnInfo(name = "emailId")
    private String emailId;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "mobileNo")
    private String mobileNo;

    @ColumnInfo(name = "userType")
    private Integer userType;

    @ColumnInfo(name = "img")
    private String img;

    @ColumnInfo(name = "fbImg")
    private String fbImg;

    @ColumnInfo(name = "enTime")
    private String enTime;

    @ColumnInfo(name = "acBal")
    private Double acBal;

    @ColumnInfo(name = "acChipsBal")
    private Double acChipsBal;

    @ColumnInfo(name = "loginSt")
    private Integer loginSt;

    @ColumnInfo(name = "onlineUser")
    private Integer onlineUser;

    @ColumnInfo(name = "sid")
    private String sid;

    @ColumnInfo(name = "passKey")
    private String passKey;

    /*
    * Getters and Setters
    * */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getInactive() {
        return inactive;
    }

    public void setInactive(Integer inactive) {
        this.inactive = inactive;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFbImg() {
        return fbImg;
    }

    public void setFbImg(String fbImg) {
        this.fbImg = fbImg;
    }

    public String getEnTime() {
        return enTime;
    }

    public void setEnTime(String enTime) {
        this.enTime = enTime;
    }

    public Double getAcBal() {
        return acBal;
    }

    public void setAcBal(Double acBal) {
        this.acBal = acBal;
    }

    public Double getAcChipsBal() {
        return acChipsBal;
    }

    public void setAcChipsBal(Double acChipsBal) {
        this.acChipsBal = acChipsBal;
    }

    public Integer getOnlineUser() {
        return onlineUser;
    }

    public void setOnlineUser(Integer onlineUser) {
        this.onlineUser = onlineUser;
    }

    public Integer getLoginSt() {
        return loginSt;
    }

    public void setLoginSt(Integer loginSt) {
        this.loginSt = loginSt;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }
}