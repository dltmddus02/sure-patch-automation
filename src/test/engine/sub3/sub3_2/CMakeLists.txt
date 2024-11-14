project(
  sub3_2
  VERSION 1.0
  DESCRIPTION "sub3_2"
)

add_library(
	${PROJECT_NAME} SHARED	
	${SOURCE_FILES}
  	${HEADER_FILES}
	c.cpp c.h
)

sub3_2 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}

set(SUB3MACRO "sub3의 매크로")
set(POCO_SUB3_MACRO "Poco${SUB3MACRO}mt")


if(UNIX)
target_link_libraries(${PROJECT_NAME} PUBLIC		
    sub4
)
endif()

add_subdirectory(sub3_2_1)
