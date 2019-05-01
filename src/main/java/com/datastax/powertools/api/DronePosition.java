package com.datastax.powertools.api;

/*
 *
 * @author Sebastián Estévez on 5/1/19.
 *
 */


import javax.json.bind.annotation.JsonbProperty;

public class DronePosition {

    //mvo.vel_x,mvo.vel_y,mvo.vel_z,mvo.pos_x,mvo.pos_y,mvo.pos_z,imu.acc_x,imu.acc_y,
    // imu.acc_z,imu.gyro_x,imu.gyro_y,imu.gyro_z,imu.q0,imu.q1,imu.q2, self.q3,imu.vg_x,imu.vg_y,imu.vg_z
    @JsonbProperty("mvo.vel_x")
    private double mvoVelX;
    @JsonbProperty("mvo.vel_y")
    private double mvoVelY;
    @JsonbProperty("mvo.vel_z")
    private double mvoVelZ;
    @JsonbProperty("mvo.pos_x")
    private double mvoPosX;
    @JsonbProperty("mvo.pos_y")
    private double mvoPosY;
    @JsonbProperty("mvo.pos_z")
    private double mvoPosZ;
    @JsonbProperty("imu.acc_x")
    private double imuAccX;
    @JsonbProperty("imu.acc_y")
    private double imuAccY;
    @JsonbProperty("imu.acc_z")
    private double imuAccZ;
    @JsonbProperty("imu.gyro_x")
    private double imuGyroX;
    @JsonbProperty("imu.gyro_y")
    private double imuGyroY;
    @JsonbProperty("imu.gyro_z")
    private double imuGyroZ;
    @JsonbProperty("imu.q0")
    private double imuQ0;
    @JsonbProperty("imu.q1")
    private double imuQ1;
    @JsonbProperty("imu.q2")
    private double imuQ2;
    @JsonbProperty("self.q3")
    private double selfQ3;
    @JsonbProperty("imu.vg_x")
    private double imuVgX;
    @JsonbProperty("imu.vg_y")
    private double imuVgY;
    @JsonbProperty("imu.vg_z")
    private double imuVgZ;

    public DronePosition() {
    }

    public double getMvoVelX() {
        return mvoVelX;
    }

    public void setMvoVelX(double mvoVelX) {
        this.mvoVelX = mvoVelX;
    }

    public double getMvoVelY() {
        return mvoVelY;
    }

    public void setMvoVelY(double mvoVelY) {
        this.mvoVelY = mvoVelY;
    }

    public double getMvoVelZ() {
        return mvoVelZ;
    }

    public void setMvoVelZ(double mvoVelZ) {
        this.mvoVelZ = mvoVelZ;
    }

    public double getMvoPosX() {
        return mvoPosX;
    }

    public void setMvoPosX(double mvoPosX) {
        this.mvoPosX = mvoPosX;
    }

    public double getMvoPosY() {
        return mvoPosY;
    }

    public void setMvoPosY(double mvoPosY) {
        this.mvoPosY = mvoPosY;
    }

    public double getMvoPosZ() {
        return mvoPosZ;
    }

    public void setMvoPosZ(double mvoPosZ) {
        this.mvoPosZ = mvoPosZ;
    }

    public double getImuAccX() {
        return imuAccX;
    }

    public void setImuAccX(double imuAccX) {
        this.imuAccX = imuAccX;
    }

    public double getImuAccY() {
        return imuAccY;
    }

    public void setImuAccY(double imuAccY) {
        this.imuAccY = imuAccY;
    }

    public double getImuAccZ() {
        return imuAccZ;
    }

    public void setImuAccZ(double imuAccZ) {
        this.imuAccZ = imuAccZ;
    }

    public double getImuGyroX() {
        return imuGyroX;
    }

    public void setImuGyroX(double imuGyroX) {
        this.imuGyroX = imuGyroX;
    }

    public double getImuGyroY() {
        return imuGyroY;
    }

    public void setImuGyroY(double imuGyroY) {
        this.imuGyroY = imuGyroY;
    }

    public double getImuGyroZ() {
        return imuGyroZ;
    }

    public void setImuGyroZ(double imuGyroZ) {
        this.imuGyroZ = imuGyroZ;
    }

    public double getImuQ0() {
        return imuQ0;
    }

    public void setImuQ0(double imuQ0) {
        this.imuQ0 = imuQ0;
    }

    public double getImuQ1() {
        return imuQ1;
    }

    public void setImuQ1(double imuQ1) {
        this.imuQ1 = imuQ1;
    }

    public double getImuQ2() {
        return imuQ2;
    }

    public void setImuQ2(double imuQ2) {
        this.imuQ2 = imuQ2;
    }

    public double getSelfQ3() {
        return selfQ3;
    }

    public void setSelfQ3(double selfQ3) {
        this.selfQ3 = selfQ3;
    }

    public double getImuVgY() {
        return imuVgY;
    }

    public void setImuVgY(double imuVgY) {
        this.imuVgY = imuVgY;
    }

    public double getImuVgZ() {
        return imuVgZ;
    }

    public void setImuVgZ(double imuVgZ) {
        this.imuVgZ = imuVgZ;
    }

    public double getImuVgX() {
        return imuVgX;
    }

    public void setImuVgX(double imuVgX) {
        this.imuVgX = imuVgX;
    }
}
