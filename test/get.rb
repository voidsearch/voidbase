#!/usr/local/bin/ruby

require 'net/http'

# check
raise "usage: ./post.rb [store] [key]" if ARGV.size < 2

# create request
http = Net::HTTP.new('localhost', 8080)
path = "/cache/#{ARGV[0]}?key=#{ARGV[1]}"

resp, data = http.get(path)

puts 'Code = ' + resp.code
puts 'Message = ' + resp.message
resp.each {|key, val| puts key + ' = ' + val}
puts data
