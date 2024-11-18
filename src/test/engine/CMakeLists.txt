project(
  root
  VERSION 1.0
  DESCRIPTION "CT Engine project"
)

set(MACRO "매크로")
set(POCO_MACRO "Poco${MACRO}mt")

add_subdirectory(sub1)
add_subdirectory(sub2)
add_subdirectory(sub3)
add_subdirectory(sub4)




// 프로젝트 구조
root / sub1 (sub4)
       / sub2 / sub2-1 (sub3-2)
       / sub3 () / sub3-1
        	/ sub3-2 (sub4) / sub3-2-1
	/ sub 4


// 각 모듈의 <참조모듈 / 참조 소스파일>
sub1 : sub4 / a.cpp a.h
sub2-1 : sub3-2 / b.cpp b.h
sub3-2 : sub4 / c.cpp c.h
sub4 : d.cpp d.h


// 각 소스파일들에 대해 최종적으로 나와야 하는 모듈
a.cpp -> sub1
b.cpp -> sub2-1
c.cpp -> sub2-1
d.cpp -> sub2-1 sub1

add-executable() : sub1, sub4
add-library(), STATIC : sub2
add-library(), SHARED : sub3_2_1, sub2_1, sub3_2