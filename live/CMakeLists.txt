cmake_minimum_required(VERSION 3.4.1)
set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../../libs)


add_library(faac
        STATIC
        IMPORTED)
set_target_properties(faac
        PROPERTIES IMPORTED_LOCATION
        ../../../../libs/${CMAKE_ANDROID_ARCH_ABI}/libfaac.a)

add_library(rtmp
        STATIC
        IMPORTED)
set_target_properties(rtmp
        PROPERTIES IMPORTED_LOCATION
        ../../../../libs/${CMAKE_ANDROID_ARCH_ABI}/librtmp.a)

add_library(x264
        STATIC
        IMPORTED)
set_target_properties(x264
        PROPERTIES IMPORTED_LOCATION
        ../../../../libs/${CMAKE_ANDROID_ARCH_ABI}/libx264.a)


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(main/cpp/include)

add_library(live
        SHARED
        src/main/cpp/AudioStream.cpp
        src/main/cpp/VideoStream.cpp
        src/main/cpp/RtmpPusher.cpp)




find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       live x264 faac rtmp
                        -landroid
                        -ljnigraphics
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )