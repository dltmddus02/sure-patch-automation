project(
  sub2_1
  VERSION 1.0
  DESCRIPTION "sub2_1"
)

add_library(
	${PROJECT_NAME} SHARED	
	${SOURCE_FILES}
  	${HEADER_FILES}
	b.cpp b.h
)

if(UNIX)
target_link_libraries(${PROJECT_NAME} PUBLIC		
    sub3_2
)
endif()

sub2_1 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}