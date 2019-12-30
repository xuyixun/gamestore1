package com.ijianjian.user.util;

public class FieldConstant {
public enum Sex {
	male, female
}

public enum UserType {
	admin, user_normal, user_member
}

public enum UserSource {
	h5, app, landingPage, sms
}

public enum PGInterFaceType {
	verificationCode, subscribe, unsubscribe, signUpStatusUpdate, datasync
}

public enum PGInterFaceStatus {
	pending, success, fail
}

public enum SubscribeType {
	subscribe, unSubscribe
}

public enum UserMemberCycleType {
	subscribe, unSubscribe, renew
}

public enum ReportGroupType {
	month, day
}
}
