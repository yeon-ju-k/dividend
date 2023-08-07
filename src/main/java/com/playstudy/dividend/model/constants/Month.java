package com.playstudy.dividend.model.constants;

public enum Month {     // 스크랩해온 날짜 월(ex.Jun)의 데이터를 숫자값으로 변경하는 클래스

    // enum값(문자열, 변환한 숫자값), 형식
    JAN("Jan", 1),
    FEB("Feb", 2),
    Mar("Mar", 3),
    APR("Apr", 4),
    MAY("May", 5),
    JUN("Jun", 6),
    JUL("Jul", 7),
    AUG("Aug", 8),
    SEP("Sep", 9),
    OCT("Oct", 10),
    NOV("Nov", 11),
    DEC("Dec", 12);

    private String s;
    private int number;

    Month(String s, int n) {    // 문자열, 반환한 숫자값
        this.s = s;
        this.number = n;
    }

    // 들어온 문자열을 -> 해당하는 월의 숫자값으로 변환해주는 기능
    public static int strToNumber(String s) {

        // enum값을 순회하면서 같은값 찾기
        for (var m : Month.values()) {
            if (m.s.equals(s)) {    // 각 enum의 문자열의 값과 들어온 문자열의 값이 같을 경우
                return m.number;    // 숫자로 반환한 값을 출력
            }
        }

        return -1;  // enum으로 설정한 값 이외의 값일 경우
    }

}

