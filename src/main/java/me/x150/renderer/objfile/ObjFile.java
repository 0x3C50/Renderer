package me.x150.renderer.objfile;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * An .obj file parser. Note that the format isn't fully implemented, and some additional issues require fixing before this can be used in a stable environment.
 * <h3>Known issues</h3>
 * <ul>
 *     <li>The format is not fully implemented</li>
 *     <li>Some UVs might get shifted or rotated depending on how complex the object is, cause is unknown</li>
 * </ul>
 * <h3>Usage</h3>
 * <ol type="1">
 *     <li>Initialize a new {@link ObjFile}</li>
 *     <li>Add all desired .mtl files using {@link ObjFile#linkMaterialFile(File)}</li>
 *     <li>Load the file with {@link ObjFile#read()}</li>
 *     <li>Render the objects with {@link Renderer3d#renderObjFile(MatrixStack, ObjFile, Vec3d, float, float, float)}</li>
 * </ol>
 */
public class ObjFile {
    /**
     * All objects in this .obj
     */
    public final Stack<ObjObject> objects = new Stack<>();
    final List<Vertex> vertices = new ArrayList<>();
    final List<Normal> normals = new ArrayList<>();
    final List<Tex> texes = new ArrayList<>();
    final List<MtlFile> mtlFiles = new ArrayList<>();
    ObjReader content;
    @Getter
    boolean initialized = false;

    /**
     * Creates a new .obj file parser from the given string contents
     *
     * @param content The .obj content
     *
     * @throws IOException When something goes wrong
     */
    public ObjFile(String content) throws IOException {
        this(new StringReader(content));
    }

    /**
     * Creates a new .obj file parser from the given input stream
     *
     * @param content The .obj content stream
     *
     * @throws IOException When something goes wrong
     */
    public ObjFile(InputStream content) throws IOException {
        this(new InputStreamReader(content));
    }

    /**
     * Creates a new .obj file parser from the given reader
     *
     * @param r The .obj content reader
     *
     * @throws IOException When something goes wrong
     */
    public ObjFile(Reader r) throws IOException {
        this.content = new ObjReader(r);
    }

    /**
     * Draws an obj object. Don't call directly unless you have a good reason to, use {@link Renderer3d#renderObjFile(MatrixStack, ObjFile, Vec3d, float, float, float)}.
     *
     * @param object The obj object to draw
     * @param bb     The buffer builder to draw to
     * @param mat    The matrix
     * @param origin The origin point relative to the camera
     * @param scaleX X scale
     * @param scaleY Y scale
     * @param scaleZ Z scale
     */
    public static void drawObject(ObjObject object, BufferBuilder bb, Matrix4f mat, Vec3d origin, float scaleX, float scaleY, float scaleZ) {
        MtlFile.Material material = object.material;
        if (material != null && material.diffuseTextureMap != null) {
            RenderSystem.setShaderTexture(0, object.material.diffuseTextureMap);
        }
        for (Face face : object.faces) {
            for (VertexBundle vertex : face.vertices) {
                Vertex vert = vertex.vert;
                Tex tex = vertex.tex;

                Normal normal = vertex.normal;
                VertexConsumer vertex1 = bb.vertex(mat, (float) (origin.x + vert.x * scaleX), (float) (origin.y + vert.y * scaleY), (float) (origin.z + vert.z * scaleZ));
                if (material != null && material.diffuseTextureMap != null) {
                    vertex1.texture(tex.x, tex.y);
                }
                if (material != null) {
                    vertex1.color(material.diffuseR, material.diffuseG, material.diffuseB, material.dissolve);
                    //                    vertex1.color(normal.x,normal.y,normal.z, 1f);
                } else {
                    vertex1.color(1f, 1f, 1f, 1f);
                }
                if (material != null && material.diffuseTextureMap != null) {
                    vertex1.normal(normal.x, normal.y, normal.z);
                }
                vertex1.next();
            }
        }
    }

    /**
     * Links a separate .mtl file to this .obj file. Call before {@link #read()}.
     *
     * @param mtlFile The path to the .mtl file
     *
     * @throws IOException When something goes wrong
     */
    public void linkMaterialFile(File mtlFile) throws IOException {
        if (this.initialized) {
            throw new IllegalStateException("Already initialized the obj");
        }
        if (!mtlFile.exists() || !mtlFile.isFile()) {
            throw new IllegalArgumentException("mtlFile does not exist or is not a file");
        }
        MtlFile e = new MtlFile(new FileReader(mtlFile));
        e.read();
        mtlFiles.add(e);
    }

    private MtlFile.Material resolveMat(String name) {
        for (MtlFile mtlFile : mtlFiles) {
            for (MtlFile.Material material : mtlFile.materialStack) {
                if (material.name.equals(name)) {
                    return material;
                }
            }
        }
        return null;
    }

    /**
     * Reads and parses this .obj file. Will resolve all materials based on {@link #mtlFiles}, which can be modified using {@link #linkMaterialFile(File)}. Call {@link #linkMaterialFile(File)} <b>BEFORE</b> calling this method.
     *
     * @throws IOException When something goes wrong
     */
    public void read() throws IOException {
        if (this.initialized) {
            throw new IllegalStateException("Already initialized");
        }
        this.initialized = true;
        while (content.peek() != -1) {
            String s = content.readStr();
            switch (s) {
                case "o" -> { // add object
                    objects.push(new ObjObject(content.readStr(), null, new ArrayList<>()));
                    content.skipLine();
                }
                case "v" -> { // vertex
                    float x = content.readFloat();
                    float y = content.readFloat(); // blender flips this one for no apparent reason
                    float z = content.readFloat();
                    float w = content.hasNextOnLine() ? content.readFloat() : 1f;
                    Vertex vt = new Vertex(x, y, z, w);
                    vertices.add(vt);
                    content.skipLine();
                }
                case "vt" -> { // vertex texture
                    float u = content.readFloat();
                    float v = content.hasNextOnLine() ? content.readFloat() : 0f;
                    float w = content.hasNextOnLine() ? content.readFloat() : 0f;
                    texes.add(new Tex(u, v, w));
                    content.skipLine();
                }
                case "vn" -> { // vertex normal
                    float x = content.readFloat();
                    float y = content.readFloat();
                    float z = content.readFloat();
                    normals.add(new Normal(x, y, z));
                    content.skipLine();
                }
                case "f" -> { // face
                    List<String> parsedVTs = new ArrayList<>();
                    while (content.hasNextOnLine()) {
                        parsedVTs.add(content.readStr());
                        content.skipWhitespace();
                    }
                    if (parsedVTs.size() < 3) {
                        throw new IllegalStateException("Expected at least 3 elements in face, got " + parsedVTs.size());
                    }
                    if (parsedVTs.size() > 4) {
                        throw new IllegalArgumentException("Only quads and tris are supported, got " + parsedVTs.size() + "-sized face");
                    }
                    List<VertexBundle> bundles = new ArrayList<>();
                    ObjObject peek = objects.peek();
                    for (String parsedVT : parsedVTs) {
                        String[] split = parsedVT.split("/");
                        VertexBundle vb = new VertexBundle(null, null, null);
                        {
                            int vPtr = Integer.parseInt(split[0]);
                            if (vPtr < 0) {
                                vPtr = vertices.size() + vPtr; // vPtr is negative, points to size - abs value
                            } else {
                                vPtr = vPtr - 1; // starts counting at 1, offset one down
                            }
                            Vertex vertex = vertices.get(vPtr);
                            vb.setVert(vertex);
                        }
                        if (split.length >= 2 && !split[1].isEmpty()) { // texture index, can be empty (vptr/<empty txptr>/nrmptr)
                            int txPtr = Integer.parseInt(split[1]);
                            if (txPtr < 0) {
                                txPtr = texes.size() + txPtr; // txPtr is negative, points to size - abs value
                            } else {
                                txPtr = txPtr - 1; // starts counting at 1, offset one down
                            }
                            Tex tex = texes.get(txPtr);
                            vb.setTex(tex);
                        }
                        if (split.length >= 3) { // normal index
                            int nrmPtr = Integer.parseInt(split[2]);
                            if (nrmPtr < 0) {
                                nrmPtr = normals.size() + nrmPtr; // nrmPtr is negative, points to size - abs value
                            } else {
                                nrmPtr = nrmPtr - 1; // starts counting at 1, offset one down
                            }
                            Normal normal = normals.get(nrmPtr);
                            vb.setNormal(normal);
                        }
                        bundles.add(vb);
                    }
                    if (bundles.size() == 4) {
                        peek.faces.add(new Face(new VertexBundle[] { bundles.get(0), bundles.get(1), bundles.get(2) })); // first tri
                        peek.faces.add(new Face(new VertexBundle[] { bundles.get(0), bundles.get(2), bundles.get(3) })); // second tri
                    } else { // nothing to do, we already have dorito shaped vertices
                        peek.faces.add(new Face(bundles.toArray(VertexBundle[]::new)));
                    }

                    content.skipLine();
                }
                case "usemtl" -> {
                    String st = content.readStr();
                    MtlFile.Material material = resolveMat(st);
                    if (material == null) {
                        throw new IllegalStateException("Tried to resolve material " + st + ", but was not found");
                    }
                    objects.peek().setMaterial(material);
                    content.skipLine();
                }
                default -> content.skipLine(); // dont know this one
            }
        }
    }

    @Data
    @AllArgsConstructor
    static class Vertex {
        float x, y, z, w;
    }

    @Data
    @AllArgsConstructor
    static class Normal {
        float x, y, z;
    }

    @Data
    @AllArgsConstructor
    static class Tex {
        float x, y, w;
    }

    @Data
    @AllArgsConstructor
    static class VertexBundle {
        Vertex vert;
        Normal normal;
        Tex tex;
    }

    @Data
    @AllArgsConstructor
    static class Face {
        VertexBundle[] vertices;
    }

    /**
     * An .obj object
     */
    @Data
    @AllArgsConstructor
    public static class ObjObject {
        /**
         * The name of this object
         */
        String name;
        /**
         * The linked material, or null
         */
        MtlFile.Material material;
        /**
         * The faces of this material
         */
        List<Face> faces;
    }
}
