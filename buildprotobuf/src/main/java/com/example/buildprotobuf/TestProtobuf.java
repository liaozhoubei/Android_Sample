package com.example.buildprotobuf;


import com.example.AClassProto;
import com.example.StudentProto;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;


public class TestProtobuf {
    // 无法执行，仅仅是演示
    public static void test() {
        byte[] bytes = makeByte();
        try {
            AClassProto.AClass aClass = AClassProto.AClass.parseFrom(bytes);
            List<StudentProto.Student> students = aClass.getStudentListList();
            for (StudentProto.Student student : students) {
                System.out.println(student.getId() + "," + student.getName() + "," + student.getAge());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }



    private static byte[] makeByte() {
        StudentProto.Student student1 = StudentProto.Student.newBuilder()
                .setName("zhang")
                .setAge(18)
                .setId(1)
                .build();

        StudentProto.Student student2 = StudentProto.Student.newBuilder()
                .setName("phil")
                .setAge(19)
                .setId(2)
                .build();

        AClassProto.AClass aClass = AClassProto.AClass.newBuilder()
                .setId(1)
                .setName("1班")
                .addStudentList(student1)
                .addStudentList(student2)
                .build();

        byte[] bytes = aClass.toByteArray();
        return bytes;
    }

}
