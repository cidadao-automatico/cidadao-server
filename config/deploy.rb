require 'capistrano/ext/multistage'
require 'capistrano/confirm'
require "rvm/capistrano"                               # Load RVM's capistrano plugin.

set :stages, %w(production staging)
set :default_stage, "staging"
set :current_frontend, "frontend-0.2"

set :user, 'ubuntu'
set :scm, :git
set :scm_verbose, false
set :keep_releases, 2

set :user, 'ubuntu'
set :use_sudo, true
set :confirm_stages, "production"

set :rvm_type, :system
set :rvm_bin_path, '/usr/local/rvm/bin'

# set :scm, :git # You can set :scm explicitly or Capistrano will make an intelligent guess based on known version control directory names
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

role :web, "julgamento.clipesebandas.com.br"                          # Your HTTP server, Apache/etc
role :app, "julgamento.clipesebandas.com.br"                          # This may be the same as your `Web` server
role :db,  "julgamento.clipesebandas.com.br", :primary => true # This is where Rails migrations will run

# if you want to clean up old releases on each deploy uncomment this:
after "deploy:update_code", "deploy:update_shared_symlinks"
after "deploy:restart", "deploy:cleanup"
after "deploy:cleanup", "npm:install"
after "npm:install", "bower:install"
after "bower:install", "grunt:build"


namespace :deploy do
  task :update_shared_symlinks do    
    run "ln -s #{File.join(deploy_to, "shared/node_modules")} #{File.join(release_path, "/frontend-0.2/node_modules")}"
    run "ln -s #{File.join(deploy_to, "shared/bower_components")} #{File.join(release_path, "/frontend-0.2/app/bower_components")}"    
  end
end

namespace :npm do
  desc 'Install npm packages'
  task :install, :roles => :web do
    run "cd #{deploy_to}/current/#{current_frontend} && rvmsudo npm install"
  end
end

namespace :bower do
  desc 'Install bower components'
  task :install, :roles => :web do
    run "cd #{deploy_to}/current/#{current_frontend} && bower install"
  end
end

namespace :grunt do
  desc 'Build javascript code using grunt'
  task :build, :roles => :web do
    run "cd #{deploy_to}/current/#{current_frontend} && grunt build"
  end
end
#grunt grunt-cli connect-livereload load-grunt-tasks
# if you're still using the script/reaper helper you will need
# these http://github.com/rails/irs_process_scripts

# If you are using Passenger mod_rails uncomment this:
# namespace :deploy do
#   task :start do ; end
#   task :stop do ; end
#   task :restart, :roles => :app, :except => { :no_release => true } do
#     run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
#   end
# end
