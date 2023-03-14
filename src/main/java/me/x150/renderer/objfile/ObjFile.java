package me.x150.renderer.objfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.x150.renderer.client.RendererMain;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.BufferUtils;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * An OBJ file parser.
 * This implementation has been tested and optimized to work well with blender exported OBJs. OBJs exported from other sources may not work as well.<br>
 * When exporting a model in blender (for use with this library), make sure the following options are set:
 * <ul>
 *     <li>Forward axis: Either X, Y, -X or -Y</li>
 *     <li><b>Up axis: Y</b></li>
 *     <li>UV Coordinates: Yes</li>
 *     <li>Normals: Yes</li>
 *     <li><b>Triangulated mesh: Yes</b>*</li>
 * </ul>
 * <b>Highlighted options</b> are especially important.<br><br>
 * *: Non-triangulated meshes may not work, triangulation may fail.<br><br>
 * <h3>Known issues</h3>
 * <ul>
 *     <li>The format is not fully implemented</li>
 * </ul>
 * <h3>Usage</h3>
 * <ol type="1">
 *     <li>Initialize a new {@link ObjFile}</li>
 *     <li>Add all desired .mtl files using {@link ObjFile#linkMaterialFile(File)}</li>
 *     <li>Load the file with {@link ObjFile#read()}</li>
 *     <li>Render the objects with {@link Renderer3d#renderObjFile(MatrixStack, ObjFile, Vec3d, float, float, float)}</li>
 * </ol>
 */
public class ObjFile implements Closeable {
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
    @Getter
    boolean closed = false;
    private int flags = 0;

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

    private void assertOpen() {
        if (closed) {
            throw new IllegalStateException("ObjFile is closed");
        }
    }

    /**
     * Sets the provided flag to true or false
     *
     * @param flag  Flag to set
     * @param state New state to set to
     *
     * @return this
     */
    public ObjFile withFlag(Flags flag, boolean state) {
        assertOpen();
        if (initialized) {
            throw new IllegalStateException("Flags must be set before initializing");
        }
        if (!state) {
            flags = flags & ~(1 << flag.pos);
        } else {
            flags = flags | (1 << flag.pos);
        }
        return this;
    }

    /**
     * Returns true if the specified flag is set to true
     *
     * @param flag Flag to check
     *
     * @return {@code true} if the flag is set, {@code false} otherwise
     */
    public boolean hasFlag(Flags flag) {
        assertOpen();
        return (flags & (1 << flag.pos)) != 0;
    }

    /**
     * Links a separate .mtl file to this .obj file. Call before {@link #read()}.
     *
     * @param mtlFile The path to the .mtl file
     *
     * @throws IOException When something goes wrong
     */
    public void linkMaterialFile(File mtlFile) throws IOException {
        assertOpen();
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
        assertOpen();
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
        assertOpen();
        if (this.initialized) {
            throw new IllegalStateException("Already initialized");
        }
        this.initialized = true;
        while (content.peek() != -1) {
            String s = content.readStr();
            switch (s) {
                case "o" -> { // add object
                    objects.push(new ObjObject(content.readStr(), null, new ArrayList<>(), null, this));
                    content.skipLine();
                }
                case "v" -> { // vertex
                    float x = content.readFloat();
                    float y = content.readFloat();
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
                    texes.add(new Tex(u, 1 - v, w));
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
                        String s1 = content.readStr().trim();
                        if (!s1.isEmpty()) {
                            parsedVTs.add(s1);
                        }
                        content.skipWhitespace();
                    }
                    if (parsedVTs.size() < 3) {
                        throw new IllegalStateException("Expected at least 3 elements in face, got " + parsedVTs.size());
                    }
                    List<VertexBundle> bundles = new ArrayList<>();
                    ObjObject peek = objects.peek();
                    for (String parsedVT : parsedVTs) {
                        String[] split = parsedVT.trim().split("/");
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

                    int n = bundles.size();
                    if (n == 4) {
                        peek.faces.add(new Face(new VertexBundle[] { bundles.get(0), bundles.get(1), bundles.get(2) })); // first tri
                        peek.faces.add(new Face(new VertexBundle[] { bundles.get(0), bundles.get(2), bundles.get(3) })); // second tri
                    } else if (n == 3) { // nothing to do, we already have dorito shaped vertices
                        peek.faces.add(new Face(bundles.toArray(VertexBundle[]::new)));
                    } else if (!hasFlag(Flags.NO_TRIANGULATION)) {
                        List<PolygonPoint> polygonPoints = new ArrayList<>(bundles.stream()
                            .map(vertexBundle -> new PolygonPoint(vertexBundle.vert.x, vertexBundle.vert.y, vertexBundle.vert.z))
                            .toList());
                        Polygon polygon = new Polygon(polygonPoints);

                        RendererMain.LOGGER.debug("Triangulating polygon " + polygon.getPoints());
                        Poly2Tri.triangulate(polygon);
                        List<DelaunayTriangle> triangles = polygon.getTriangles();
                        for (DelaunayTriangle triangle : triangles) {
                            TriangulationPoint[] points = triangle.points;
                            VertexBundle[] vb = new VertexBundle[points.length];
                            for (int i = 0; i < points.length; i++) {
                                TriangulationPoint point = points[i];
                                Optional<VertexBundle> first = bundles.stream()
                                    .filter(vertexBundle -> vertexBundle.vert.x == point.getX() && vertexBundle.vert.y == point.getY() && vertexBundle.vert.z == point.getZ())
                                    .findFirst();
                                Normal nrm = first.map(vertexBundle -> vertexBundle.normal).orElse(null);
                                Tex tex = first.map(vertexBundle -> vertexBundle.tex).orElse(null);
                                vb[i] = new VertexBundle(new Vertex((float) point.getX(), (float) point.getY(), (float) point.getZ(), 0), nrm, tex);
                            }
                            peek.faces.add(new Face(vb));
                        }
                    } else {
                        throw new IllegalStateException("Don't know how to handle " + n + "-sized polygon");
                    }

                    content.skipLine();
                }
                case "usemtl" -> {
                    String st = content.readStr();
                    if (!st.isBlank()) {
                        MtlFile.Material material = resolveMat(st);
                        if (material == null) {
                            if (!hasFlag(Flags.IGNORE_MISSING_MATERIALS)) {
                                throw new IllegalStateException("Tried to resolve material " + st + ", but was not found");
                            } else {
                                RendererMain.LOGGER.warn("Tried to resolve material " + st + ", but was not found");
                            }
                        } else {
                            objects.peek().setMaterial(material);
                        }
                    }
                    content.skipLine();
                }
                default -> content.skipLine(); // dont know this one
            }
        }
    }

    @Override
    public void close() throws IOException {
        assertOpen();
        for (ObjObject object : this.objects) {
            object.close();
        }
        content.close();
    }

    /**
     * Flags for the obj file
     */
    @AllArgsConstructor
    public enum Flags {
        /**
         * Instead of throwing when encountering missing materials, do nothing with them
         */
        IGNORE_MISSING_MATERIALS(0),
        /**
         * Render a wireframe of the obj's geometry instead of the obj itself
         */
        RENDER_WIREFRAME(1),
        /**
         * Do not perform triangulation when facing a polygon with more than 4 faces, throw instead
         */
        NO_TRIANGULATION(2);
        final int pos;
    }

    /**
     * A vertex
     */
    @Data
    @AllArgsConstructor
    public static class Vertex {
        /**
         * X coordinate
         */
        float x;
        /**
         * Y coordinate
         */
        float y;
        /**
         * Z coordinate
         */
        float z;
        /**
         * W coordinate
         */
        float w;
    }

    /**
     * A normal direction
     */
    @Data
    @AllArgsConstructor
    public static class Normal {
        /**
         * Normal x
         */
        float x;
        /**
         * Normal Y
         */
        float y;
        /**
         * Normal Z
         */
        float z;
    }

    /**
     * An UV coordinate
     */
    @Data
    @AllArgsConstructor
    public static class Tex {
        /**
         * U coordinate
         */
        float x;
        /**
         * V coordinate
         */
        float y;
        /**
         * W coordinate
         */
        float w;
    }

    /**
     * A bundle of Vertex, Normal and UV coordinates
     */
    @Data
    @AllArgsConstructor
    public static class VertexBundle {
        /**
         * The vertex coordinate
         */
        Vertex vert;
        /**
         * The normal
         */
        Normal normal;
        /**
         * UV coordinates
         */
        Tex tex;
    }

    /**
     * A face
     */
    @Data
    @AllArgsConstructor
    public static class Face {
        /**
         * The vertices of this face
         */
        VertexBundle[] vertices;
    }

    /**
     * An .obj object
     */
    @Data
    @AllArgsConstructor
    public static class ObjObject implements Closeable {
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
        /**
         * A buffer holding the drawn representation of this object, after {@link #bake()} is called
         */
        VertexBuffer buffer;
        /**
         * The parent, which contains this object
         */
        ObjFile parent;

        @Override
        public void close() {
            if (buffer != null) {
                buffer.close();
                buffer = null;
            }
        }

        /**
         * Bakes this object into its {@link #buffer}. Called by {@link Renderer3d#renderObjObject(ObjObject, Matrix4f, Vec3d, float, float, float)}
         *
         * @deprecated For internal use only
         */
        @Deprecated
        public void bake() {
            if (buffer != null) {
                buffer.close();
            }
            if (parent.hasFlag(Flags.RENDER_WIREFRAME)) {
                VertexFormat vf = VertexFormats.POSITION_COLOR;
                Tessellator t = Tessellator.getInstance();
                BufferBuilder bb = t.getBuffer();

                bb.begin(VertexFormat.DrawMode.DEBUG_LINES, vf);

                Renderer3d.drawObjObjectWireframe(this, bb, new Matrix4f(), Vec3d.ZERO, 1, 1, 1);

                BufferBuilder.BuiltBuffer end = bb.end();
                buffer = BufferUtils.createVbo(end);
            } else {
                VertexFormat vf = (this.getMaterial() != null && this.getMaterial()
                    .getDiffuseTextureMap() != null) ? VertexFormats.POSITION_TEXTURE_COLOR_NORMAL : VertexFormats.POSITION_COLOR;
                Tessellator t = Tessellator.getInstance();
                BufferBuilder bb = t.getBuffer();

                bb.begin(VertexFormat.DrawMode.TRIANGLES, vf);

                Renderer3d.drawObjObject(this, bb, new Matrix4f(), Vec3d.ZERO, 1, 1, 1);

                BufferBuilder.BuiltBuffer end = bb.end();
                buffer = BufferUtils.createVbo(end);
            }
        }
    }
}
