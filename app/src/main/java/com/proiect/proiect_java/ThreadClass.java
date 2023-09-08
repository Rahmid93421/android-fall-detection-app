package com.proiect.proiect_java;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class ThreadClass extends Thread {
    public double mAccelVal;
    private double oldmAccelVal = 99999999;
    public double min, max, max1 = -999999999;
    private double min_th_pre_fall = 8.00, max_th_pre_fall = 13.42, max_diff = 12.00, min_th_pos_fall = 9.35, max_th_pos_fall = 10.45;
    private double fall_dt = 2000, st_dt = 7500, rec_dt = 12500;
    /*
         0 = normal state
         1 = fall state
         2 = impact state
         3 = recovery state
         4 = send popup
         5 = unconscious state
     */
    public int state = 0, mAccelArrayInd = 0;
    private long currTimeMilis = 0;
    private TelegramBot bot = new TelegramBot("<YOUR TOKEN>");
    public double latitude, longitude;

    public void run() {
        while (true) {
            if (mAccelVal != 0) {
                switch (state) {
                    case 0: {
                        if (mAccelVal < min_th_pre_fall) {
                            if (mAccelVal <= oldmAccelVal) {
                                min = mAccelVal;
                                oldmAccelVal = mAccelVal;
                            } else {
                                state = 1;
                                oldmAccelVal = 99999999;
                                currTimeMilis = System.currentTimeMillis();
                            }
                        }
                        break;
                    }
                    case 1: {
                        if (System.currentTimeMillis() <= currTimeMilis + fall_dt) {
                            if (max1 < mAccelVal) {
                                max1 = mAccelVal;
                            }
                        } else {
                            if (max1 > max_th_pre_fall) {
                                max = max1;
                                state = 2;
                            } else {
                                state = 0;
                            }
                            max1 = -999999999;
                        }
                        break;
                    }
                    case 2: {
                        if (max - min <= max_diff) {
                            state = 3;
                            currTimeMilis = System.currentTimeMillis();
                        } else {
                            state = 0;
                        }
                        break;
                    }
                    case 3: {
                        if (System.currentTimeMillis() > currTimeMilis + st_dt) {
                            if (System.currentTimeMillis() < currTimeMilis + rec_dt) {
                                if (mAccelVal < min_th_pos_fall || mAccelVal > max_th_pos_fall) {
                                    state = 0;
                                }
                            } else {
                                state = 4;
                                currTimeMilis = System.currentTimeMillis();
                            }
                        }
                        break;
                    }
                    case 4: {
                        if (System.currentTimeMillis() > currTimeMilis + 5000) {
                            state = 5;
                        }
                        break;
                    }
                    case 5: {
                        SendResponse response = bot.execute(new SendMessage(5735047398L, "Am cazut, hai si ajuta-ma!!!"));
                        bot.execute(new SendMessage(5735047398L, "Date GPS: latitudine - " + latitude + " ; longitudine - " + longitude));
                        state = 0;
                        break;
                    }
                }
                ;
            }
        }
    }
}
