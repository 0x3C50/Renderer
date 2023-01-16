package me.x150.renderer.objfile;

import java.io.IOException;
import java.io.Reader;

class ObjReader {
    Reader parent;
    int next;

    public ObjReader(Reader parent) throws IOException {
        this.parent = parent;
        read();
    }

    public int read() throws IOException {
        int nx = parent.read();
        int r = next;
        next = nx;
        return r;
    }

    public void close() throws IOException {
        parent.close();
    }

    public int peek() {
        return next;
    }

    public void skipLine() throws IOException {
        int r;
        while ((r = peek()) != -1 && r != '\n') {
            read();
        }
        read(); // one more additional read to get the newline out
    }

    public void skipWhitespace() throws IOException {
        int r;
        while ((r = peek()) != -1 && r == ' ') {
            read();
        }
    }

    public String readStr() throws IOException {
        StringBuilder s = new StringBuilder();
        int r;
        while ((r = peek()) != -1 && r != ' ' && r != '\n') {
            s.append((char) read());
        }
        skipWhitespace(); // skip to next prop
        return s.toString();
    }

    public boolean hasNextOnLine() throws IOException {
        skipWhitespace();
        return peek() != '\n' && peek() != '#';
    }

    public float readFloat() throws IOException {
        return Float.parseFloat(readStr());
    }
}
