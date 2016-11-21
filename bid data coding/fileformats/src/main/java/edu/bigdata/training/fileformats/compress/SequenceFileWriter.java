/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bigdata.training.fileformats.compress;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Metadata;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.DefaultCodec;

/**
 *
 * @author myhome
 */
public class SequenceFileWriter {

    public static void main(String[] args) throws IOException {
        String uri = "output";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(uri);
        IntWritable key = new IntWritable();
        Text value = new Text();
        File infile = new File("src/main/resources/input.txt");
        SequenceFile.Writer writer = null;
        try {
            writer = SequenceFile.createWriter(conf, Writer.file(path),
                    Writer.keyClass(key.getClass()),
                    Writer.valueClass(value.getClass()),
                    Writer.bufferSize(fs.getConf().getInt("io.file.buffer.size", 4096)),
                    Writer.replication(fs.getDefaultReplication()),
                    Writer.blockSize(1073741824),
                    Writer.compression(SequenceFile.CompressionType.BLOCK, new DefaultCodec()),
                    Writer.progressable(null),
                    Writer.metadata(new Metadata()));
            int ctr = 100;
            List<String> lines = FileUtils.readLines(infile);
            for (String line : lines) {
                key.set(ctr++);
                value.set(line);
                if (ctr < 150) {
                    System.out.printf("[%s]\t%s\t%s\n", writer.getLength(), key, value);
                }
                writer.append(key, value);
            }
        } finally {
            IOUtils.closeStream(writer);
        }
    }
}
