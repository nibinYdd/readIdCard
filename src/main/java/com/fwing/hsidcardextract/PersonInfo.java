package com.fwing.hsidcardextract;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * @Author: Fwing
 * @CreateDate: 2020/7/15 16:21
 */
public class PersonInfo {

    public enum IDCARD_TYPE {
        /**
         * 身份证
         */
        SFZ,
        /**
         * 港澳台居住证
         */
        GATJZZ,
        /**
         * 外国人永久居留证
         */
        WGRYJJLZ
    }
    private IDCARD_TYPE idType = IDCARD_TYPE.SFZ;
    private String _PeopleName = "";
    private String _Sex = "";
    private String _People = "";
    private Date _BirthDay = new Date();
    private String _Addr = "";
    private String _IDCard = "";
    private String _Department = "";
    private String _StartDate = "";
    private String _EndDate = "";
    private byte[] _FpDate = new byte[1024];
    private static byte[] _bmpdata = new byte[1024];
    private static byte[] _wltdata = new byte[1024];
    private String m_certType = "";
    private String m_strNationCode = "";
    private String m_strChineseName = "";
    private String m_strCertVer = "";
    private String m_issuesNum = "";
    private String m_PassCheckID = "";
    private String firstFpPosition = "";
    private byte firstFpQuality = 0;
    private String secondFpPosition = "";
    private byte secondFpQuality = 0;

    public PersonInfo() {
    }

    public IDCARD_TYPE getIdType() {
        return idType;
    }

    public void setIdType(IDCARD_TYPE idType) {
        this.idType = idType;
    }

    public String getcertType() {
        return this.m_certType;
    }

    public void setcertType(String value) {
        this.m_certType = value;
    }

    public String getstrNationCode() {
        return this.m_strNationCode;
    }

    public void setstrNationCode(String value) {
        this.m_strNationCode = value;
    }

    public String getstrChineseName() {
        return this.m_strChineseName;
    }

    public void setstrChineseName(String value) {
        this.m_strChineseName = value;
    }

    public String getissuesNum() {
        return this.m_issuesNum;
    }

    public void setissuesNum(String value) {
        this.m_issuesNum = value;
    }

    public String getPassCheckID() {
        return this.m_PassCheckID;
    }

    public void setPassCheckID(String value) {
        this.m_PassCheckID = value;
    }

    public String getstrCertVer() {
        return this.m_strCertVer;
    }

    public void setstrCertVer(String value) {
        this.m_strCertVer = value;
    }

    public String getPeopleName() {
        return this._PeopleName;
    }

    public void setPeopleName(String value) {
        this._PeopleName = value;
    }

    public void setSex(String value) {
        this._Sex = value;
    }

    public String getSex() {
        return this._Sex;
    }

    public String getPeople() {
        return this._People;
    }

    public void setPeople(String value) {
        this._People = value;
    }

    public Date getBirthDay() {
        return this._BirthDay;
    }

    public void setBirthDay(Date value) {
        this._BirthDay = value;
    }

    public String getAddr() {
        return this._Addr;
    }

    public void setAddr(String value) {
        this._Addr = value;
    }

    public String getIDCard() {
        return this._IDCard;
    }

    public void setIDCard(String value) {
        this._IDCard = value;
    }

    public String getDepartment() {
        return this._Department;
    }

    public void setDepartment(String value) {
        this._Department = value;
    }

    public String getStartDate() {
        return this._StartDate;
    }

    public void setStartDate(String value) {
        this._StartDate = value;
    }

    public String getEndDate() {
        return this._EndDate;
    }

    public void setEndDate(String value) {
        this._EndDate = value;
    }

    public byte[] getFpDate() {
        return this._FpDate;
    }

    public void setFpDate(byte[] value) {
        this._FpDate = value;
    }

    public byte[] getbmpdata() {
        return _bmpdata;
    }

    protected void setbmpdata(byte[] bmpdata) {
        _bmpdata = bmpdata;
    }

    public byte[] getwltdata() {
        return _wltdata;
    }

    public void setwltdata(byte[] wltdata) {
        _wltdata = wltdata;
    }

    public String getFirstFpPosition() {
        return firstFpPosition;
    }

    public void setFirstFpPosition(String firstFpPosition) {
        this.firstFpPosition = firstFpPosition;
    }

    public int getFirstFpQuality() {
        return firstFpQuality;
    }

    public void setFirstFpQuality(byte firstFpQuality) {
        this.firstFpQuality = firstFpQuality;
    }

    public String getSecondFpPosition() {
        return secondFpPosition;
    }

    public void setSecondFpPosition(String secondFpPosition) {
        this.secondFpPosition = secondFpPosition;
    }

    public int getSecondFpQuality() {
        return secondFpQuality;
    }

    public void setSecondFpQuality(byte secondFpQuality) {
        this.secondFpQuality = secondFpQuality;
    }

    @NonNull
    @Override
    public String toString() {
        String type = getIdType() == IDCARD_TYPE.SFZ ? "身份证":getIdType() == IDCARD_TYPE.GATJZZ ? "港澳台居住证":getIdType() == IDCARD_TYPE.WGRYJJLZ ? "外国人永久居留证":"null";
        return "证件类型："+type+"\n"
                + "姓名：" + getPeopleName()+ "\n"
                + "性别：" + getSex() + "\n"
                + "民族："+ getPeople() + "\n"
                + "出生日期："+ getBirthDay()+ "\n"
                + "地址："+ getAddr() + "\n"
                + "身份号码：" + getIDCard()+ "\n"
                + "签发机关：" + getDepartment() + "\n"
                + "有效期限：" + getStartDate() + "-" + getEndDate() + "\n"
                + "指纹  信息：第一枚指纹注册成功。指位："+getFirstFpPosition()+"。指纹质量："+getFirstFpQuality()+" \n"
                + "指纹  信息：第二枚指纹注册成功。指位："+getSecondFpPosition()+"。指纹质量："+getSecondFpQuality()+" \n"
                + "签发次数："+ getissuesNum() + "\n"
                + "通行证号码："+ getPassCheckID() + "\n"
                + "中文名称："+ getstrChineseName() + "\n"
                + "永久居留证号："+ getIDCard() + "\n"
                + "国籍："+ getstrNationCode() + "\n"
                + "证件版本号：" + getstrCertVer() + "\n";
    }
}
