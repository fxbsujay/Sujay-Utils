/*
 * MIT License
 *
 * Copyright (c) 2018 Keep-Tech
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.susu.serializer;


import com.susu.serializer.taira.TairaData;
import com.susu.serializer.taira.annotation.ParamField;

public class Baz implements TairaData {

    @ParamField(order = 0) private int bazinga;

    public Baz() {}

    public Baz(int bazinga) {
        this.bazinga = bazinga;
    }

    public int getBazinga() {
        return bazinga;
    }

    public void setBazinga(int bazinga) {
        this.bazinga = bazinga;
    }

    @Override
    public String toString() {
        return "Baz{" + "bazinga=" + bazinga + '}';
    }
}
