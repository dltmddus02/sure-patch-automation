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

if(UNIX)
	target_link_libraries(${PROJECT_NAME} PRIVATE
		-Wl,--start-group
		cs_common
		libUTIL_LIB64
		PocoFoundationmt
		ucommon
		-Wl,--end-group
		${CMAKE_DL_LIBS}
	)
elseif(WIN32)
	target_link_libraries(${PROJECT_NAME} PRIVATE
		cs_common
		libUTIL_LIB64
		PocoFoundationmt
	)
endif()


// 프로젝트 구조
root / sub1
       / sub2 / sub2-1
       / sub3 / sub3-1
        	/ sub3-2 / sub3-2-1
	/ sub 4

add_executable() : sub1
add_library(), STATIC : sub2-1
add_library(), SHARED : sub3-2-1
