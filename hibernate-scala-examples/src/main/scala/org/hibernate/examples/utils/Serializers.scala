package org.hibernate.examples.utils


object Serializers {

    lazy val log = Logger(this.getClass)
    lazy val serializer = new BinarySerializer()


    def serializeObject[T <: AnyRef](graph: T): Array[Byte] = serializer.serialize[T](graph)

    def deserializeObject[T <: AnyRef](bytes: Array[Byte], clazz: Class[T]): T =
        serializer.deserialize[T](bytes, clazz)

    def copyObject[T <: AnyRef](graph: T): T = {
        if (graph == null)
            null.asInstanceOf[T]
        else
            deserializeObject[T](serializeObject(graph), graph.getClass.asInstanceOf[Class[T]])
    }
}
